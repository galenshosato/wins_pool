import pytest
import sys
from datetime import datetime
from decimal import Decimal

sys.path.append("/home/galensato/Development/code/coding-projects/wins_pool")

from app import app
from server.extensions import db
from server.models import (
    WinPool,
    Year,
    User,
    Week,
    WeeklyWin,
    Game,
    Record,
    Team,
    UserDraftPick,
)
from server.functions.update_functions import calculate_strength_of_schedule


class TestWinsPoolRoutes:
    """Tests related to the Wins Pool Routes"""

    @pytest.fixture
    def sample_data(self):
        with app.app_context():
            user = User(name="Galen")
            year = Year(year=2023)
            week = Week(week_number=5)
            db.session.add_all([user, year, week])
            db.session.commit()

            win_pool = WinPool(user=user, year=year, total_wins=10)

            db.session.add(win_pool)
            db.session.commit()

            weekly_win = WeeklyWin(
                user_id=user.id, year_id=year.id, week_id=week.id, wins=2
            )

            db.session.add(weekly_win)
            db.session.commit()

            yield {
                "user": user,
                "year": year,
                "week": week,
                "win_pool": win_pool,
                "weekly_win": weekly_win,
            }

            db.session.delete(user)
            db.session.delete(year)
            db.session.delete(week)
            db.session.delete(win_pool)
            db.session.delete(weekly_win)
            db.session.commit()

    def test_wins_pool_by_year_success(self, sample_data):
        response = app.test_client().get(f"/{sample_data['year'].year}/wins-pool")
        assert response.status_code == 200
        assert response.json[0]["total_wins"] == 10
        assert response.json[0]["user"] == sample_data["user"].name
        assert response.json[0]["year"] == sample_data["year"].year

    def test_wins_pool_by_year_and_user_id_success(self, sample_data):
        response = app.test_client().get(
            f"/{sample_data['year'].year}/{sample_data['user'].id}/wins-pool"
        )
        assert response.status_code == 200
        assert response.json["total_wins"] == 10
        assert response.json["user"] == "Galen"
        assert response.json["year"] == 2023

    def test_weekly_win_by_year_and_user_and_week_success(self, sample_data):
        response = app.test_client().get(
            f"/{sample_data['year'].year}/{sample_data['week'].week_number}/{sample_data['user'].id}/weekly-wins"
        )
        assert response.status_code == 200
        assert response.json["wins"] == 2
        assert response.json["week"] == 5


class TestUpdateRoutes:
    """Tests for Updating Records"""

    @pytest.fixture
    def sample_data(self):
        with app.app_context():
            year = Year(year=2022, isActive=True)
            week = Week(week_number=20, isActive=True)
            week2 = Week(week_number=21, isActive=False)
            team1 = Team(team_name="test1")
            team2 = Team(team_name="test2")
            team3 = Team(team_name="test3")
            team4 = Team(team_name="test4")
            db.session.add_all([year, week, week2, team1, team2, team3, team4])
            db.session.commit()

            game1 = Game(
                home_team=1,
                away_team=2,
                winner=1,
                loser=2,
                isTie=False,
                timeStarted=datetime.utcnow(),
                started=True,
                week=week,
                year_id=1,
            )
            game2 = Game(
                home_team=3,
                away_team=4,
                isTie=True,
                timeStarted=datetime.utcnow(),
                started=True,
                week=week,
                year_id=1,
            )

            record1 = Record(year_id=1, team=team1)
            record2 = Record(year_id=1, team=team2)
            record3 = Record(year_id=1, team=team3)
            record4 = Record(year_id=1, team=team4)

            db.session.add_all([record1, record2, record3, record4, game1, game2])
            db.session.commit()

            yield {
                "year": year,
                "week": week,
                "week2": week2,
                "game1": game1,
                "game2": game2,
            }

            # Clean up the test data
            db.session.delete(year)
            db.session.delete(week)
            db.session.delete(week2)
            db.session.delete(game1)
            db.session.delete(game2)
            db.session.query(Record).delete()
            db.session.delete(team1)
            db.session.delete(team2)
            db.session.delete(team3)
            db.session.delete(team4)
            db.session.commit()

    def test_update_wins_for_week_route(self, sample_data):
        response = app.test_client().patch(
            f"/{sample_data['year'].year}/update-wins-week"
        )
        assert response.status_code == 200

        # Check if the records were updated correctly
        team1_record = Record.query.filter_by(
            team_id=1, year_id=sample_data["year"].id
        ).first()
        team2_record = Record.query.filter_by(
            team_id=2, year_id=sample_data["year"].id
        ).first()
        team3_record = Record.query.filter_by(
            team_id=3, year_id=sample_data["year"].id
        ).first()
        team4_record = Record.query.filter_by(
            team_id=4, year_id=sample_data["year"].id
        ).first()

        assert team1_record.wins == 1
        assert team2_record.losses == 1
        assert team2_record.ties == 0
        assert team3_record.ties == 1
        assert team3_record.wins == 0
        assert team4_record.losses == 0
        assert team4_record.ties == 1


class TestStrengthOfScheduleRoutes:
    """Tests related to Strength of Schedule"""

    @pytest.fixture
    def sample_data(self):
        with app.app_context():
            week = Week(week_number=2)
            week2 = Week(week_number=3)
            year = Year(year=2026)
            team1 = Team(team_name="test1")
            team2 = Team(team_name="test2")
            team3 = Team(team_name="test3")
            user1 = User(name="user1")
            user2 = User(name="user2")
            db.session.add_all([year, team1, team2, team3, user1, user2, week, week2])
            db.session.commit()

            draft_team1 = UserDraftPick(team=team1, year_id=year.id, user=user1)
            draft_team2 = UserDraftPick(team=team2, year_id=year.id, user=user1)
            draft_team3 = UserDraftPick(team=team3, year_id=year.id, user=user2)

            db.session.add_all([draft_team1, draft_team2, draft_team3])
            db.session.commit()

            team1_record = Record(
                team=team1,
                wins=2,
                losses=4,
                ties=0,
                strength_of_schedule=0.456,
                year_id=year.id,
            )
            team2_record = Record(
                team=team2,
                wins=6,
                losses=4,
                ties=1,
                strength_of_schedule=0.667,
                year_id=year.id,
            )
            team3_record = Record(
                team=team3,
                wins=11,
                losses=5,
                ties=0,
                strength_of_schedule=0.125,
                year_id=year.id,
            )
            week2_game = Game(
                week=week, home_team=team1.id, away_team=team2.id, year_id=year.id
            )

            week3_game = Game(
                week=week2, home_team=team2.id, away_team=team3.id, year_id=year.id
            )

            db.session.add_all(
                [team1_record, team2_record, team3_record, week2_game, week3_game]
            )
            db.session.commit()

            yield {"week": week, "year": year, "team2": team2}

            db.session.query(User).delete()
            db.session.query(Year).delete()
            db.session.query(Week).delete()
            db.session.query(UserDraftPick).delete()
            db.session.query(Team).delete()
            db.session.query(Record).delete()
            db.session.query(Game).delete()
            db.session.commit()

    def test_get_strength_of_schedule(self, sample_data):
        response = app.test_client().get(
            f"/{sample_data['year'].year}/strength-of-schedule"
        )
        response_object = response.json

        assert response.status_code == 200
        assert len(response.json) == 3
        assert response_object[0]["user"] == "user1"
        assert response_object[1]["record"]["team"]["team_name"] == "test2"
        assert response_object[2]["record"]["strength_of_schedule"] == "0.125"

    def test_update_strength_of_schedule(self, sample_data):
        response = app.test_client().patch(
            f"{sample_data['year'].year}/{sample_data['week'].week_number}/update-strength-of-schedule"
        )

        assert response.status_code == 201
        assert (
            response.json["Success"]
            == "You have successfully updated the league's strength of schedule"
        )

        updated_record = Record.query.filter_by(team=sample_data["team2"]).first()

        assert updated_record.wins == 6
        assert updated_record.losses == 4
        assert updated_record.ties == 1
        assert updated_record.opponent_wins == 13
        assert updated_record.opponent_losses == 9
        assert updated_record.opponent_ties == 0
        assert updated_record.strength_of_schedule == Decimal("0.591")
