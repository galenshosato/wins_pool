from flask import Flask, jsonify, request, make_response, session as browser_session
from server import db, migrate


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


if __name__ == "__main__":
    app.run(host="localhost", port="5555", debug=True)
