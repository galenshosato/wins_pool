import sys

sys.path.append("/home/galensato/Development/code/coding-projects/wins_pool")

from decimal import Decimal

from app import app
from server.extensions import db
from server.models import User, Year, Team, DraftPick, UserDraftPick


class TestUser:
    """User Model in models.py"""

    def test_user_has_correct_columns(self):
        """has columns for name, email, password, and money"""
        with app.app_context():
            user1 = User(
                name="Test", email="test@test.com", password="test", money_owed=5.45
            )

            db.session.add(user1)
            db.session.commit()

            assert user1.name == "Test"
            assert user1.email == "test@test.com"
            assert user1.password == "test"
            assert user1.money_owed == Decimal("5.45")

            db.session.delete(user1)
            db.session.commit()


class TestYear:
    """Year model in models.py"""

    def test_year_has_correct_columns(self):
        """Has columns for year"""

        with app.app_context():
            year1 = Year(year=2023)

            db.session.add(year1)
            db.session.commit()

            assert year1.year == 2023

            db.session.delete(year1)
            db.session.commit()


class TestTeam:
    """Team model in models.py"""

    def test_team_has_correct_columns(self):
        """Has columns for team_name"""
        with app.app_context():
            team1 = Team(team_name="Philadelphia Eagles")

            db.session.add(team1)
            db.session.commit()

            assert team1.team_name == "Philadelphia Eagles"

            db.session.delete(team1)
            db.session.commit()


class TestDraftPick:
    """DraftPick model in models.py"""

    def test_draftpick_has_correct_columns(self):
        """Has columns for pick_number"""
        with app.app_context():
            draft_pick1 = DraftPick(pick_number=1)

            db.session.add(draft_pick1)
            db.session.commit()

            assert draft_pick1.pick_number == 1

            db.session.delete(draft_pick1)
            db.session.commit()


class TestUserDraftPick:
    """UserDraftPick join table in models.py"""

    def test_userdraftpick_has_correct_columsn(self):
        """Has correct columns and references for user, team, year, and draft_pick"""
        with app.app_context():
            user = User(
                name="Test",
                email="test@test.com",
                password="password",
                money_owed=10.00,
            )
            team = Team(team_name="Team 1")
            year = Year(year=2023)
            draft_pick = DraftPick(pick_number=20)

            db.session.add_all([user, team, year, draft_pick])
            db.session.commit()

            user_draft_pick = UserDraftPick(
                user=user, team=team, year=year, draft_pick=draft_pick
            )
            db.session.add(user_draft_pick)
            db.session.commit()

            saved_user_draft_pick = UserDraftPick.query.filter(
                UserDraftPick.user == user
            ).first()

            # Validate Columns
            assert saved_user_draft_pick is not None
            assert saved_user_draft_pick.user_id == user.id
            assert saved_user_draft_pick.team_id == team.id
            assert saved_user_draft_pick.year_id == year.id
            assert saved_user_draft_pick.draft_pick_id == draft_pick.id

            # Validate the relationships
            assert saved_user_draft_pick.user == user
            assert saved_user_draft_pick.team == team
            assert saved_user_draft_pick.year == year
            assert saved_user_draft_pick.draft_pick == draft_pick

            # Validate attributes of related models
            assert saved_user_draft_pick.user.name == "Test"
            assert saved_user_draft_pick.team.team_name == "Team 1"
            assert saved_user_draft_pick.year.year == 2023
            assert saved_user_draft_pick.draft_pick.pick_number == 20

            db.session.query(UserDraftPick).delete()
            db.session.query(User).delete()
            db.session.query(DraftPick).delete()
            db.session.query(Year).delete()
            db.session.query(Team).delete()
            db.session.commit()
