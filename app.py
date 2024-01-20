from flask import Flask, jsonify, request, make_response, session as browser_session
from server import (
    db,
    migrate,
    User,
    Week,
    Year,
    Team,
    DraftPick,
    UserDraftPick,
    WinPool,
    WeeklyWin,
    Record,
    Game,
    calculate_strength_of_schedule,
)


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
            or "favorite_team" not in data
        ):
            return make_response({"error": "Invalid JSON data"}, 400)

        name = data.get("name")
        email = data.get("email")
        password = data.get("password")
        favorite_team = data.get("favorite_team")
        new_user = User(
            name=name,
            email=email,
            password=password,
            money_owed=0.00,
            favorite_team=favorite_team,
        )
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


@app.route("/<int:year>/<int:id>/teams", methods=["GET", "PATCH"])
def teams_by_user_id_and_year(year, id):
    draft_year = Year.query.filter_by(year=year).first()
    user = User.query.filter_by(id=id).first()

    if request.method == "GET":
        user_teams_for_year = UserDraftPick.query.filter_by(
            year_id=draft_year.id, user_id=user.id
        ).all()
        teams = [team.team.to_dict() for team in user_teams_for_year]

        return make_response(teams, 200)

    elif request.method == "PATCH":
        # JSON request = {
        # "draftId": draft_pick_id,
        # "teamId": team_id }
        try:
            data = request.get_json()

            # Check for missing or invalid JSON data
            if not data or "draftId" not in data or "teamId" not in data:
                return make_response({"error": "Invalid JSON data"}, 400)

            draft_id = data.get("draftId")
            team_id = data.get("teamId")
            print("Draft Id is: ", draft_id)
            print("Team Id is: ", team_id)
            print("User Id is: ", user.id)

            # Check if team exists
            team = Team.query.filter_by(id=team_id).first()
            if not team:
                return make_response({"error": "Team not found"}, 404)

            draft_pick = DraftPick.query.filter_by(id=draft_id).first()

            user_pick = UserDraftPick.query.filter_by(
                user_id=user.id, year_id=draft_year.id, draft_pick_id=draft_id
            ).first()
            print(UserDraftPick.query.all())
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


# Routes for Wins Pool
# This gives you all of the wins-pool instances for a given year
@app.route("/<int:year>/wins-pool")
def wins_pool_by_year(year):
    year = Year.query.filter_by(year=year).first()
    wins_pools = WinPool.query.filter_by(year_id=year.id).all()
    wins_pools_to_dict = [win_pool.to_dict() for win_pool in wins_pools]
    return make_response(wins_pools_to_dict, 200)


# This route returns the win-pool for a given year and given user
@app.route("/<int:year>/<int:id>/wins-pool")
def win_pool_by_year_and_user_id(year, id):
    current_year = Year.query.filter_by(year=year).first()
    user = User.query.filter_by(id=id).first()
    win_pool = WinPool.query.filter_by(user_id=user.id, year_id=current_year.id).first()
    return make_response(win_pool.to_dict(), 200)


# This route returns the weekly win instance for a given user, for a given week, in a given year
@app.route("/<int:year>/<int:week>/<int:id>/weekly-wins")
def get_weekly_wins_by_year_week_user_id(year, week, id):
    current_year = Year.query.filter_by(year=year).first()
    current_week = Week.query.filter_by(week_number=week).first()
    current_user = User.query.filter_by(id=id).first()
    weekly_win = WeeklyWin.query.filter_by(
        user_id=current_user.id, week_id=current_week.id, year_id=current_year.id
    ).first()
    return make_response(weekly_win.to_dict(), 200)


# Getting the team information for the admin SoS view by year
@app.route("/<int:year>/strength-of-schedule")
def get_strength_of_schedule_by_year(year):
    current_year = Year.query.filter_by(year=year).first()
    userteams_in_pool = UserDraftPick.query.filter_by(year_id=current_year.id).all()
    strength_of_schedule_response = []
    for userteam in userteams_in_pool:
        record = Record.query.filter_by(team_id=userteam.team_id).first()
        response = {"user": userteam.user.name, "record": record.to_dict()}
        strength_of_schedule_response.append(response)
    return make_response(strength_of_schedule_response, 200)


# Route that updates the records at the end of the week. Theoretically, this will be a backend process prompted by a git action
@app.route("/<int:year>/update-wins-week", methods=["PATCH"])
def update_wins_for_week(year):
    week = Week.query.filter_by(isActive=True).first()
    year = Year.query.filter_by(year=year).first()
    games = Game.query.filter_by(week_id=week.id).all()
    tie = []
    for game in games:
        if game.isTie == True:
            tie.append(game.home_team)
            tie.append(game.away_team)
    if len(tie) > 1:
        for tying_team_id in tie:
            tying_team_record = Record.query.filter_by(
                team_id=tying_team_id, year_id=year.id
            ).first()
            tying_team_record.ties += 1
            db.session.add(tying_team_record)
            db.session.commit()

    winners = [game.winner for game in games if game.winner is not None]
    losers = [game.loser for game in games if game.loser is not None]

    for winning_team_id in winners:
        winning_team_record = Record.query.filter_by(
            team_id=winning_team_id, year_id=year.id
        ).first()
        winning_team_record.wins += 1
        db.session.add(winning_team_record)
        db.session.commit()

    for losing_team_id in losers:
        losing_team_record = Record.query.filter_by(
            team_id=losing_team_id, year_id=year.id
        ).first()
        losing_team_record.losses += 1
        db.session.add(losing_team_record)
        db.session.commit()

    if week.week_number == 18:
        next_week = Week.query.filter_by(week_number=1).first()
        next_week.isActive = True
        db.session.add(next_week)
        db.session.commit()
        week.isActive = False
        db.session.add(week)
        db.session.commit()
        return make_response({"Success": "You have updated this weeks winners"}, 200)
    else:
        next_week = Week.query.filter_by(id=((week.id) + 1)).first()
        next_week.isActive = True
        db.session.add(next_week)
        db.session.commit()
        week.isActive = False
        db.session.add(week)
        db.session.commit()
        return make_response({"Success": "You have updated this weeks winners"}, 200)


@app.route("/<int:year>/<int:week>/update-strength-of-schedule", methods=["PATCH"])
def update_strength_of_schedule(week, year):
    current_week = Week.query.filter_by(week_number=week).first()
    current_year = Year.query.filter_by(year=year).first()
    teams = Team.query.all()
    for team in teams:
        home_games = Game.query.filter(
            Game.home_team == team.id,
            Game.year_id == current_year.id,
            Game.week >= current_week.week,
        ).all()
        away_games = Game.query.filter(
            Game.away_team == team.id,
            Game.year_id == current_year.id,
            Game.week >= current_week.week,
        ).all()
        home_opponents = [opponent.away_team for opponent in home_games]
        away_opponents = [opponent.home_team for opponent in away_games]
        all_opponents = home_opponents + away_opponents

        total_opponent_wins = 0
        total_opponent_losses = 0
        total_opponent_ties = 0

        for opponent_id in all_opponents:
            opponent_record = Record.query.filter_by(
                team_id=opponent_id, year_id=current_year.id
            ).first()
            total_opponent_wins += opponent_record.wins
            total_opponent_losses += opponent_record.losses
            total_opponent_ties += opponent_record.ties

        team_record = Record.query.filter_by(
            team_id=team.id, year_id=current_year.id
        ).first()
        team_record.opponent_wins = total_opponent_wins
        team_record.opponent_losses = total_opponent_losses
        team_record.opponent_ties = total_opponent_ties

        strength_of_schedule = calculate_strength_of_schedule(
            total_opponent_wins, total_opponent_losses, total_opponent_ties
        )
        team_record.strength_of_schedule = strength_of_schedule
        db.session.add(team_record)
        db.session.commit()
    return make_response(
        {"Success": "You have successfully updated the league's strength of schedule"},
        201,
    )


if __name__ == "__main__":
    app.run(host="localhost", port="5555", debug=True)
