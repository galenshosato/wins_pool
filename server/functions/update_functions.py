import requests
import json


# Calculates numerical strength of schedule
def calculate_strength_of_schedule(wins, losses, ties):
    total_games = wins + losses + ties
    return wins / total_games


# Returns an array of the winners for the week from the ESPN API
def get_winners_from_ESPN(year, week):
    winner_array = []
    espn_response = requests.get(
        f"https://site.api.espn.com/apis/site/v2/sports/football/nfl/scoreboard?dates={year}&seasontype=2&week={week}"
    )
    espn_object = json.loads(espn_response.text)
    games = espn_object["events"]
    for game in games:
        teams = game["competitions"][0]["competitors"]
        for team in teams:
            if team["winner"] == True:
                name = team["team"]["name"]
                winner_array.append(name)
    return winner_array
