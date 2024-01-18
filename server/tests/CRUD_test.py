import pytest
import requests
import sys

sys.path.append("/home/galensato/Development/code/coding-projects/wins_pool")

from app import app
from server.extensions import db
from server.models import User, Year, Team, DraftPick, UserDraftPick


class TestCRUDUser:
    @pytest.fixture
    def sample_data(self):
        with app.app_context():
            user1, user2, user3 = (
                User(
                    name="Morgan",
                    email="test@test.com",
                    password="password",
                    deleted=False,
                    money_owed=0.00,
                ),
                User(
                    name="Anthony",
                    email="test2@test.com",
                    password="password2",
                    deleted=False,
                    money_owed=0.00,
                ),
                User(
                    name="Patrick",
                    email="test3@test.com",
                    password="password3",
                    deleted=False,
                    money_owed=0.00,
                ),
            )

            db.session.add_all([user1, user2, user3])
            db.session.commit()

            yield {"users": [user1, user2, user3]}

            db.session.delete(user1)
            db.session.delete(user2)
            db.session.delete(user3)

            db.session.commit()

    def test_get_all_users(self, sample_data):
        response = app.test_client().get("/users")

        assert response.status_code == 200

        resp_data = response.get_json()
        assert len(resp_data) == 3
        assert resp_data[0]["name"] == "Morgan"
        assert resp_data[1]["email"] == "test2@test.com"
        assert resp_data[2]["email"] == "test3@test.com"

    def test_create_user(self, sample_data):
        data = {
            "name": "Galen",
            "email": "test4@test.com",
            "password": "password4",
            "favorite_team": "Patriots",
        }
        response = app.test_client().post("/users", json=data)

        assert response.status_code == 201
        assert response.content_type == "application/json"

        created_user = response.get_json()
        assert created_user["name"] == "Galen"
        assert created_user["email"] == "test4@test.com"
        assert float(created_user["money_owed"]) == 0.00

        get_response = app.test_client().get("/users")
        assert get_response.status_code == 200
        get_resp_data = get_response.get_json()
        assert len(get_resp_data) == 4

        delete_user = User.query.filter_by(name="Galen").first()
        db.session.delete(delete_user)
        db.session.commit()
