import requests
import json
import sys

sys.path.append("/home/galensato/Development/code/coding-projects/wins_pool")

from app import app
from server.extensions import db
from server.models import Game, Team, Week, Year


def create_game_from_week(events, week, year):
    with app.app_context():
        for event in events:
            current_week = Week.query.filter(Week.week_number == week).first()
            current_year = Year.query.filter(Year.year == year).first()
            time_started = event["date"]

            home_team_name = event["competitions"][0]["competitors"][0]["team"]["name"]
            home_team_object = Team.query.filter(
                Team.team_name == home_team_name
            ).first()

            away_team_name = event["competitions"][0]["competitors"][1]["team"]["name"]
            away_team_object = Team.query.filter(
                Team.team_name == away_team_name
            ).first()

            new_game = Game(
                home_team=home_team_object.id,
                away_team=away_team_object.id,
                timeStarted=time_started,
                year_id=current_year.id,
                week=current_week,
            )

            db.session.add(new_game)
            db.session.commit()


def create_full_schedule(year):
    for n in range(1, 19):
        espn_response = requests.get(
            f"https://site.api.espn.com/apis/site/v2/sports/football/nfl/scoreboard?dates={year}&seasontype=2&week={n}"
        )
        espn_object = json.loads(espn_response.text)
        game_events = espn_object["events"]
        create_game_from_week(game_events, n, year)


if __name__ == "__main__":
    with app.app_context():
        db.session.query(Game).delete()
        db.session.commit()
        create_full_schedule(2023)
