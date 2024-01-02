from server.extensions import db
from datetime import datetime


# Write the following tables:
# Games:
# -Wins(Hold off until you finish the Draft Feature)
# -Pick'em (user_id, year_id, 18 weeks with arrays of team_ids and points)
# -Survivor(user_id, year_id, 18 weeks with a single team_id)
# -Predictions(user_id, year_id, and I need to think about how I want to structure this)


class User(db.Model):
    __tablename__ = "users"

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String)
    email = db.Column(db.String)
    password = db.Column(db.String)
    deleted = db.Column(db.Boolean)
    money_owed = db.Column(db.Numeric(precision=10, scale=2))

    def __repr__(self):
        return f"<User Name={self.name}, Id={self.id}, Email={self.email}, Money Owed={self.money_owed}>"

    def to_dict(self):
        return {
            "id": self.id,
            "name": self.name,
            "email": self.email,
            "deleted": self.deleted,
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


class Week(db.Model):
    __tablename__ = "weeks"

    id = db.Column(db.Integer, primary_key=True)
    week_number = db.Column(db.Integer)

    def __repr__(self):
        return f"<Week Id={self.id} Week={self.week_number}>"

    def to_dict(self):
        return {"id": self.id, "week": self.week_number}


class Team(db.Model):
    __tablename__ = "teams"

    id = db.Column(db.Integer, primary_key=True)
    team_name = db.Column(db.String)

    def __repr__(self):
        return f"<Team id={self.id}, name={self.team_name}"

    def to_dict(self):
        return {"id": self.id, "team_name": self.team_name}


class DraftPick(db.Model):
    __tablename__ = "draft_picks"

    id = db.Column(db.Integer, primary_key=True)
    pick_number = db.Column(db.Integer)

    def __repr__(self):
        return f"<Draft Pick Number = {self.pick_number}>"

    def to_dict(self):
        return {"pick_number": self.pick_number}


class UserDraftPick(db.Model):
    __tablename__ = "user_draft_picks"

    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey("users.id"))
    draft_pick_id = db.Column(db.Integer, db.ForeignKey("draft_picks.id"))
    year_id = db.Column(db.Integer, db.ForeignKey("years.id"))
    team_id = db.Column(db.Integer, db.ForeignKey("teams.id"))

    user = db.relationship("User", backref="user_draft_picks")
    draft_pick = db.relationship("DraftPick", backref="user_draft_picks")
    year = db.relationship("Year", backref="user_draft_picks")
    team = db.relationship("Team", backref="user_draft_picks")


class Record(db.Model):
    __tablename__ = "records"

    id = db.Column(db.Integer, primary_key=True)
    team_id = db.Column(db.Integer, db.ForeignKey("teams.id"))
    year_id = db.Column(db.Integer, db.ForeignKey("years.id"))
    wins = db.Column(db.Integer)
    losses = db.Column(db.Integer)
    ties = db.Column(db.Integer)

    team = db.relationship("Team", backref="record")
    year = db.relationship("Year", backref="record")

    def __repr__(self):
        return f"<Record Team={self.team.team_name}, Year={self.year.year}, Wins={self.wins}, Losses={self.losses}, Ties={self.ties}>"

    def to_dict(self):
        return {
            "id": self.id,
            "team": self.team.team_name,
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

    user = db.relationship("User", backref="win_pool")
    year = db.relationship("Year", backref="win_pool")

    def __repr__(self):
        return f"<User Wins User={self.user.name}, Total Wins={self.total_wins}, Year = {self.year.year}>"

    def to_dict(self):
        return {
            "id": self.id,
            "user": self.user.name,
            "year": self.year.year,
            "total_wins": self.total_wins,
            "teams_drafted": [
                pick.team.team_name for pick in self.user.user_draft_picks
            ],
        }


class WeeklyWin(db.Model):
    __tablename__ = "weekly_wins"

    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey("users.id"))
    year_id = db.Column(db.Integer, db.ForeignKey("years.id"))
    week_id = db.Column(db.Integer, db.ForeignKey("weeks.id"))
    wins = db.Column(db.Integer)

    week = db.relationship("Week", backref="weekly_win")

    def __repr__(self):
        return f"<Wins For The Week: Week={self.week_number_id}, Wins={self.wins}>"

    def to_dict(self):
        return {
            "id": self.id,
            "week": self.week.week_number,
            "wins": self.wins,
        }
