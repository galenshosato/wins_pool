from server.extensions import db
from datetime import datetime
from sqlalchemy import Numeric

# Write the following tables: Year (to track different years), Team (with the individual NFL teams)
# Games:
# -Record (This will have a year_id, a user_id (if the team is picked), and the wins/losses/ties)
# -Wins (This will have a user_id, a year_id, and 18 weeks with wins data that gets added each week)
# -Pick'em (user_id, year_id, 18 weeks with arrays of team_ids and points)
# -Survivor(user_id, year_id, 18 weeks with a single team_id)
# -Predictions(user_id, year_id, and I need to think about how I want to structure this)


class User(db.Model):
    __tablename__ = "users"

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String)
    email = db.Column(db.String)
    password = db.Column(db.String)
    money_owed = db.Column(Numeric(precision=10, scale=2))
