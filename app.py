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


@app.route("/assign_team_to_user", methods=["PATCH"])
# JSON request = {
# "userId": user_id,
# "year": year,
# "draftId": draft_pick_id,
# "teamId": team_id}
def assign_team_to_user():
    try:
        data = request.json()

    except Exception as e:
        # Handle unexpected errors
        print(f"Error:{e}")
        db.session.rollback()
        return make_response({"error": "internal server error"}, 500)


if __name__ == "__main__":
    app.run(host="localhost", port="5555", debug=True)
