import pytest
import sys

sys.path.append("/home/galensato/Development/code/coding-projects/wins_pool")

from app import app
from server.extensions import db
from server.models import User, Year, Team, DraftPick, UserDraftPick


class TestDraftAssignments:
    """Tests related to draft assignments"""

    # JSON request should look like:
    # {"name": user,
    #  "year": year,
    #  "draftPicks: [1,2,3]"}

    @pytest.fixture
    def sample_data(self):
        with app.app_context():
            user = User(name="galen")
            year = Year(year=2023)
            draft_pick_1 = DraftPick(pick_number=1)
            draft_pick_5 = DraftPick(pick_number=5)
            draft_pick_10 = DraftPick(pick_number=10)

            db.session.add_all([user, year, draft_pick_1, draft_pick_5, draft_pick_10])
            db.session.commit()

            yield {
                "user": user,
                "year": year,
                "picks": [draft_pick_1, draft_pick_5, draft_pick_10],
            }

            # Clean up after the test
            db.session.delete(user)
            db.session.delete(year)
            db.session.delete(draft_pick_1)
            db.session.delete(draft_pick_5)
            db.session.delete(draft_pick_10)
            db.session.commit()

    def test_draft_assignments_success(self, sample_data):
        data = {"name": "galen", "year": 2023, "draftPicks": [1, 5, 10]}
        response = app.test_client().post("/assign_draft_picks", json=data)

        assert response.status_code == 201
        assert response.content_type == "application/json"
        assert (
            response.json["Success"]
            == f"Draft picks were successfully assigned to {data['name']}"
        )

        test_user = User.query.filter(User.name == "galen").first()
        picks = [pick.draft_pick.pick_number for pick in test_user.user_draft_picks]
        assert sorted(picks) == [1, 5, 10]

        draft_pick = UserDraftPick.query.filter_by(
            user_id=test_user.id, draft_pick_id=sample_data["picks"][1].id
        ).first()
        assert draft_pick.draft_pick.pick_number == 5

    def test_draft_assignments_invalid_json(self):
        data = {"invalid_key": "galen"}  # Missing required keys
        response = app.test_client().post("/assign_draft_picks", json=data)

        assert response.status_code == 400
        assert response.content_type == "application/json"
        assert response.json["error"] == "Invalid JSON data"

    def test_draft_assignments_user_not_found(self):
        data = {"name": "nonexistent_user", "year": 2023, "draftPicks": [1, 5, 10]}
        response = app.test_client().post("/assign_draft_picks", json=data)

        assert response.status_code == 404
        assert response.content_type == "application/json"
        assert response.json["error"] == "User not found"

    def test_draft_assignments_year_not_found(self, sample_data):
        # Modify the fixture data to create a situation where the year doesn't exist
        data = {"name": "galen", "year": 2024, "draftPicks": [1, 5, 10]}
        response = app.test_client().post("/assign_draft_picks", json=data)

        assert response.status_code == 404
        assert response.content_type == "application/json"
        assert response.json["error"] == "Year not found"

    def test_draft_assignments_invalid_draft_picks_format(self, sample_data):
        data = {
            "name": "galen",
            "year": 2023,
            "draftPicks": "invalid_format",
        }  # Not a list
        response = app.test_client().post("/assign_draft_picks", json=data)

        assert response.status_code == 400
        assert response.content_type == "application/json"
        assert response.json["error"] == "Invalid format for draftPicks"

    def test_draft_assignments_invalid_pick_number(self, sample_data):
        data = {
            "name": "galen",
            "year": 2023,
            "draftPicks": ["invalid"],
        }  # Invalid pick number
        response = app.test_client().post("/assign_draft_picks", json=data)

        assert response.status_code == 400
        assert response.content_type == "application/json"
        assert response.json["error"] == "Invalid pick number"

    def test_draft_assignments_draft_pick_not_found(self, sample_data):
        data = {
            "name": "galen",
            "year": 2023,
            "draftPicks": [100],
        }  # Non-existent pick number
        response = app.test_client().post("/assign_draft_picks", json=data)

        assert response.status_code == 404
        assert response.content_type == "application/json"
        assert response.json["error"] == "Draft pick 100 not found"

    def test_draft_assignments_internal_server_error(self, sample_data, monkeypatch):
        # Simulate an internal server error during database operations
        def mock_commit():
            raise Exception("Simulated internal server error")

        monkeypatch.setattr(db.session, "commit", mock_commit)

        data = {"name": "galen", "year": 2023, "draftPicks": [1, 5, 10]}
        response = app.test_client().post("/assign_draft_picks", json=data)

        assert response.status_code == 500
        assert response.content_type == "application/json"
        assert response.json

    def test_get_pick_numbers_by_year(self, sample_data):
        data = {"name": "galen", "year": 2023, "draftPicks": [1, 5, 10]}
        app.test_client().post("/assign_draft_picks", json=data)
        user = sample_data["user"]
        year = sample_data["year"]
        response = app.test_client().get(f"/{year.year}/{user.id}/pick_numbers")

        assert response.status_code == 200
        assert len(response.json) == 3
        assert response.json[1]["pick_number"] == 5


class TestTeamAssignments:
    """Tests related to team assignments via the draft"""

    # JSON request = {
    # "userId": user_id,
    # "yearId": year_id,
    # "draftId": draft_pick_id,
    # "teamId": team_id }

    @pytest.fixture
    def sample_data(self):
        with app.app_context():
            user = User(name="galen")
            year = Year(year=2023)
            draft_pick_1 = DraftPick(pick_number=1)
            draft_pick_5 = DraftPick(pick_number=5)

            team1 = Team(team_name="team1")
            team2 = Team(team_name="team2")

            db.session.add_all([user, year, draft_pick_1, draft_pick_5, team1, team2])
            db.session.commit()

            test_team_assignment1 = UserDraftPick(
                user=user,
                year=year,
                draft_pick=draft_pick_1,
            )

            test_team_assignment2 = UserDraftPick(
                user=user,
                year=year,
                draft_pick=draft_pick_5,
            )

            db.session.add_all([test_team_assignment1, test_team_assignment2])
            db.session.commit()

            yield {
                "user": user,
                "year": year,
                "pick1": draft_pick_1,
                "pick5": draft_pick_5,
                "team1": team1,
                "team2": team2,
            }

            # Clean Up
            db.session.delete(test_team_assignment1)
            db.session.delete(test_team_assignment2)
            db.session.delete(user)
            db.session.delete(year)
            db.session.delete(draft_pick_1)
            db.session.delete(draft_pick_5)
            db.session.delete(team1)
            db.session.delete(team2)

            db.session.commit()

    def test_team_assignments_success(self, sample_data):
        user = sample_data["user"]
        year = sample_data["year"]

        data1 = {
            "draftId": sample_data["pick1"].id,
            "teamId": sample_data["team1"].id,
        }
        data2 = {
            "draftId": sample_data["pick5"].id,
            "teamId": sample_data["team2"].id,
        }

        response1 = app.test_client().patch(f"/{year.year}/{user.id}/teams", json=data1)

        assert response1.status_code == 200
        assert (
            response1.json["Success"]
            == f"{sample_data['team1'].team_name} has been assigned to {user.name} with pick {sample_data['pick1'].pick_number}"
        )

        # Retrieve the specific UserDraftPick instance from the collection
        user_draft_pick1 = UserDraftPick.query.filter_by(
            user_id=user.id, draft_pick_id=sample_data["pick1"].id
        ).first()

        assert user_draft_pick1.team == sample_data["team1"]

        response2 = app.test_client().patch(f"/{year.year}/{user.id}/teams", json=data2)
        assert response2.status_code == 200
        assert (
            response2.json["Success"]
            == f"{sample_data['team2'].team_name} has been assigned to {user.name} with pick {sample_data['pick5'].pick_number}"
        )

        # Retrieve the specific UserDraftPick instance from the collection
        user_draft_pick2 = UserDraftPick.query.filter_by(
            user_id=user.id, draft_pick_id=sample_data["pick5"].id
        ).first()

        assert user_draft_pick2.team == sample_data["team2"]

        # test_user = User.query.filter_by(id=sample_data["user"].id).first()
        # teams = [team.team.team_name for team in test_user.user_draft_picks]

        # assert len(teams) == 2
        # assert teams == ["team1", "team2"]

        get_response = app.test_client().get(f"/{year.year}/{user.id}/teams")

        assert get_response.status_code == 200
        assert len(get_response.json) == 2
        assert get_response.json[1]["team_name"] == "team2"
