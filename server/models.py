from extensions import db
from datetime import datetime


# Write the following tables:
# Games:
# -Wins (Adjust so that there is a week table and then a reference to week in the appropriate tables)
# -Pick'em (user_id, year_id, 18 weeks with arrays of team_ids and points)
# -Survivor(user_id, year_id, 18 weeks with a single team_id)
# -Predictions(user_id, year_id, and I need to think about how I want to structure this)


class User(db.Model):
    __tablename__ = "users"

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String)
    email = db.Column(db.String)
    password = db.Column(db.String)
    money_owed = db.Column(db.Numeric(precision=10, scale=2))

    def __repr__(self):
        return f"<User Name={self.name}, Id={self.id}, Email={self.email}, Money Owed={self.money_owed}>"

    def to_dict(self):
        return {
            "id": self.id,
            "name": self.name,
            "email": self.email,
            "money_owed": self.money_owed,
        }


class Year(db.Model):
    __tablename__ = "years"

    id = db.Column(db.Integer, primary_key=True)
    year = db.Column(db.Integer)

    def __repr__(self):
        return f"<Year id={self.id}, year={self.year}>"

    def to_dict(self):
        return {"id": self.id, "year": self.year}


class Team(db.Model):
    __tablename__ = "teams"

    id = db.Column(db.Integer, primary_key=True)
    team_name = db.Column(db.String)

    def __repr__(self):
        return f"<Team id={self.id}, name={self.team_name}"

    def to_dict(self):
        return {"id": self.id, "team_name": self.team_name}


class Record(db.Model):
    __tablename__ = "records"

    id = db.Column(db.Integer, primary_key=True)
    team_id = db.Column(db.Integer, db.ForeignKey("teams.id"))
    user_id = db.Column(db.Integer, db.ForeignKey("users.id"), nullable=True)
    year_id = db.Column(db.Integer, db.ForeignKey("years.id"))
    wins = db.Column(db.Integer)
    losses = db.Column(db.Integer)
    ties = db.Column(db.Integer)

    team = db.relationship("Team", backref="record")
    year = db.relationship("Year", backref="record")
    user = db.relationship("User", backref="record")

    def __repr__(self):
        return f"<Record Team={self.team.team_name}, User={self.user.name}, Year={self.year.year}, Wins={self.wins}, Losses={self.losses}, Ties={self.ties}>"

    def to_dict(self):
        return {
            "id": self.id,
            "team": self.team.team_name,
            "user": self.user.name,
            "year": self.year.year,
            "wins": self.wins,
            "losses": self.losses,
            "ties": self.ties,
        }


class WinPool(db.Model):
    __tablename__ = "wins_pool"

    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey("users.id"))
    year_id = db.Column(db.Integer, db.ForeignKey("years.id"))
    total_wins = db.Column(db.Integer)

    user = db.relationship("User", backref="winPool")

    def __repr__(self):
        return f"<User Wins User={self.user.name}, Total Wins={self.total_wins}>"

    def to_dict(self):
        return {"id": self.id, "user": self.user.name, "total_wins": self.total_wins}


class WeeklyWin(db.Model):
    __tablename__ = "weekly_wins"

    id = db.Column(db.Integer, primary_key=True)
    win_pool_id = db.Column(db.Integer, db.ForeignKey("wins_pool.id"))
    week_number = db.Column(db.Integer)
    wins = db.Column(db.Integer)

    def __repr__(self):
        return f"<Wins For The Week: Week={self.week_number}, Wins={self.wins}>"

    def to_dict(self):
        return {
            "id": self.id,
            "win_pool_id": self.win_pool_id,
            "week": self.week_number,
            "wins": self.wins,
        }
