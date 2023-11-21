import pytest
import requests
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

    # def test_team_assignments_to_user(self):
    #     pass
