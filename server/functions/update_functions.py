def calculate_strength_of_schedule(wins, losses, ties):
    total_games = wins + losses + ties
    return wins / total_games
