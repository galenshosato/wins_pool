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


# Getting all users and creating a new user
@app.route("/users", methods=["GET", "POST"])
def users():
    if request.method == "GET":
        users = User.query.all()
        if not users:
            return make_response({"error": "No users found. Please add users"}, 404)

        users_dict = [user.to_dict() for user in users]
        return make_response(users_dict, 200)

    elif request.method == "POST":
        data = request.get_json()
        if (
            not data
            or "name" not in data
            or "email" not in data
            or "password" not in data
        ):
            return make_response({"error": "Invalid JSON data"}, 400)

        name = data.get("name")
        email = data.get("email")
        password = data.get("password")
        new_user = User(name=name, email=email, password=password, money_owed=0.00)
        db.session.add(new_user)
        db.session.commit()

        return make_response(new_user.to_dict(), 201)


# Routes for Draft Feature
@app.route("/assign_draft_picks", methods=["POST"])

# JSON request should look like:
# {"name": user,
#  "year": year,
#  "draftPicks: [1,2,3]"}


def assign_draft_picks():
    try:
        data = request.get_json()

        # Check for missing or invalid JSON data
        if (
            not data
            or "name" not in data
            or "year" not in data
            or "draftPicks" not in data
        ):
            return make_response({"error": "Invalid JSON data"}, 400)

        user_name = data.get("name")
        year_name = data.get("year")

        # Check if user exists
        user = User.query.filter(User.name == user_name).first()
        if not user:
            return make_response({"error": "User not found"}, 404)

        # Check if year exists
        year = Year.query.filter(Year.year == year_name).first()
        if not year:
            return make_response({"error": "Year not found"}, 404)

        draft_picks = data.get("draftPicks")

        # Check if draft_picks is a list
        if not isinstance(draft_picks, list):
            return make_response({"error": "Invalid format for draftPicks"}, 400)

        for pick in draft_picks:
            # Check if pick is a valid integer
            try:
                pick = int(pick)
            except ValueError:
                return make_response({"error": "Invalid pick number"}, 400)

            # Check if draft_pick exists
            draft_pick = DraftPick.query.filter(DraftPick.pick_number == pick).first()
            if not draft_pick:
                return make_response({"error": f"Draft pick {pick} not found"}, 404)

            new_draft_pick_assignment = UserDraftPick(
                user=user, year=year, draft_pick=draft_pick
            )
            db.session.add(new_draft_pick_assignment)
            db.session.commit()
        return make_response(
            {"Success": f"Draft picks were successfully assigned to {user_name}"}, 201
        )
    except Exception as e:
        # Handle unexpected errors
        print(f"Error:{e}")
        db.session.rollback()
        return make_response({"error": "Internal server error"}, 500)


@app.route("/<int:year>/<int:id>/pick_numbers")
def get_picks_by_user_id_and_year(year, id):
    draft_year = Year.query.filter_by(year=year).first()
    user = User.query.filter_by(id=id).first()
    user_picks_for_year = UserDraftPick.query.filter_by(
        year_id=draft_year.id, user_id=user.id
    ).all()
    picks = [pick.draft_pick.to_dict() for pick in user_picks_for_year]
    return make_response(picks, 200)


@app.route("/<int:year>/draft_order")
def manage_draft_order(year):
    draft_year = Year.query.filter_by(year=year).first()
    if not draft_year:
        return make_response({"error": "Please select a valid year"}, 404)

    draft_picks = UserDraftPick.query.filter_by(year_id=draft_year.id).all()

    draft_order = [
        {
            "name": draft.user.name,
            "pick_number": draft.draft_pick.pick_number,
            "team": draft.team.team_name,
        }
        for draft in draft_picks
    ]

    draft_order = sorted(draft_order, key=lambda x: x["pick_number"])

    return make_response(draft_order, 200)


@app.route("/assign_team_to_user", methods=["PATCH"])
# JSON request = {
# "userId": user_id,
# "yearId": year_id,
# "draftId": draft_pick_id,
# "teamId": team_id }
def assign_team_to_user():
    try:
        data = request.get_json()

        # Check for missing or invalid JSON data
        if (
            not data
            or "userId" not in data
            or "yearId" not in data
            or "draftId" not in data
            or "teamId" not in data
        ):
            return make_response({"error": "Invalid JSON data"}, 400)

        user_id = data.get("userId")
        year_id = data.get("yearId")
        draft_id = data.get("draftId")
        team_id = data.get("teamId")

        # Check if team exists
        team = Team.query.filter_by(id=team_id).first()
        if not team:
            return make_response({"error": "Team not found"}, 404)

        user = User.query.filter_by(id=user_id).first()
        draft_pick = DraftPick.query.filter_by(id=draft_id).first()

        user_pick = UserDraftPick.query.filter_by(
            user_id=user_id, year_id=year_id, draft_pick_id=draft_id
        ).first()
        if not user_pick:
            return make_response({"error": "UserDraftPick object not found"}, 404)

        user_pick.team_id = team_id

        db.session.add(user_pick)
        db.session.commit()

        return make_response(
            {
                "Success": f"{team.team_name} has been assigned to {user.name} with pick {draft_pick.pick_number}"
            },
            200,
        )

    except Exception as e:
        # Handle unexpected errors
        print(f"Error:{e}")
        db.session.rollback()
        return make_response({"error": "Internal server error"}, 500)


@app.route("/<int:year>/<int:id>/teams")
def get_teams_by_user_id_and_year(year, id):
    draft_year = Year.query.filter_by(year=year).first()
    user = User.query.filter_by(id=id).first()
    user_teams_for_year = UserDraftPick.query.filter_by(
        year_id=draft_year.id, user_id=user.id
    ).all()
    teams = [team.team.to_dict() for team in user_teams_for_year]

    return make_response(teams, 200)


if __name__ == "__main__":
    app.run(host="localhost", port="5555", debug=True)
