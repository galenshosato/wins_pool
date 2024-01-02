import pytest
import sys

sys.path.append("/home/galensato/Development/code/coding-projects/wins_pool")

from app import app
from server.extensions import db
from server.models import WinPool, Year, User, Week, WeeklyWin


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
            weekly_win = WeeklyWin(
                user_id=user.id, year_id=year.id, week_id=week.id, wins=2
            )

            db.session.add_all([win_pool, weekly_win])
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
