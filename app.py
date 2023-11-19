from flask import Flask, jsonify, request, make_response, session as browser_session
from server import db, migrate, User, Week, Year, Team, DraftPick, UserDraftPick


app = Flask(__name__)
app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///app.db"
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
app.json.compact = False
app.secret_key = "woah that is a secret key"
db.init_app(app)
migrate.init_app(app, db)


@app.route("/")
def home():
    return "Wins Pool API"


# Routes for Draft Feature
@app.route("/assign_draft_picks", methods=["POST"])
# JSON request should look like:
# {"name": user,
#  "year": year,
#  "draftPicks: [1,2,3]"}
def assign_draft_picks():
    data = request.get_json()
    user_name = data.get("name")
    year_name = data.get("year")
    user = User.query.filter(User.name == user_name).first()
    year = Year.query.filter(Year.name == year_name).first()
    draft_picks = data.get("draftPicks")

    for pick in draft_picks:
        draft_pick = DraftPick.query.filter(DraftPick.pick_number == pick).first()
        new_draft_pick_assignment = UserDraftPick(
            user=user, year=year, draft_pick=draft_pick
        )
        db.session.add(new_draft_pick_assignment)
        db.session.commit()
    return make_response(
        {"Success": f"Draft picks were successfully assigned to {user_name}"}, 201
    )


if __name__ == "__main__":
    app.run(host="localhost", port="5555", debug=True)
