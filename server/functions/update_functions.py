import requests
import json


# Calculates numerical strength of schedule
def calculate_strength_of_schedule(wins, losses, ties):
    total_games = wins + losses + ties
    return wins / total_games


# Returns an array of the winners for the week from the ESPN API
def get_winners_from_ESPN(year, week):
    espn_response = requests.get(
        f"https://site.api.espn.com/apis/site/v2/sports/football/nfl/scoreboard?dates={year}&seasontype=2&week={week}"
    )
    espn_object = json.loads(espn_response.text)
    # TODO: Go through each game and find the winner, then store in an array and return
