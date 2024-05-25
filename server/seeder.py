import sys

sys.path.append("/home/galensato/Development/code/coding-projects/wins_pool")

from app import app
from server.extensions import db
from server.models import Team, Week, Year

if __name__ == "__main__":
    with app.app_context():
        print("Clearing Team, Week, Year ...")
        db.session.query(Team).delete()
        db.session.query(Week).delete()
        db.session.query(Year).delete()
        db.session.commit()

        # print("Seeding weeks...")
        # week_array = []
        # for n in range(1, 19):
        #     new_week = Week(week_number=n)
        #     week_array.append(new_week)
        # db.session.add_all(week_array)
        # db.session.commit()

        # print("Seeding years...")
        # year_2023 = Year(year=2023)
        # year_2024 = Year(year=2024)
        # db.session.add_all([year_2023, year_2024])
        # db.session.commit()

        # print("Seeding all teams...")
        # # AFC East
        # patriots = Team(team_name="Patriots", color="002a5c", alt_color="c60c30")
        # bills = Team(team_name="Bills", color="00338d", alt_color="d50a0a")
        # dolphins = Team(team_name="Dolphins", color="008e97", alt_color="fc4c02")
        # jets = Team(team_name="Jets", color="115740", alt_color="ffffff")
        # db.session.add_all([patriots, bills, dolphins, jets])
        # db.session.commit()

        # # AFC North
        # ravens = Team(team_name="Ravens", color="29126f", alt_color="000000")
        # steelers = Team(team_name="Steelers", color="000000", alt_color="ffb612")
        # browns = Team(team_name="Browns", color="472a08", alt_color="ff3c00")
        # bengals = Team(team_name="Bengals", color="fb4f14", alt_color="000000")
        # db.session.add_all([ravens, steelers, browns, bengals])
        # db.session.commit()

        # # AFC West
        # chiefs = Team(team_name="Chiefs", color="e31837", alt_color="ffb612")
        # broncos = Team(team_name="Broncos", color="0a2343", alt_color="fc4c02")
        # raiders = Team(team_name="Raiders", color="000000", alt_color="a5acaf")
        # chargers = Team(team_name="Chargers", color="0080c6", alt_color="ffc20e")
        # db.session.add_all([chiefs, broncos, raiders, chargers])
        # db.session.commit()

        # # AFC South
        # jaguars = Team(team_name="Jaguars", color="007487", alt_color="d7a22a")
        # texans = Team(team_name="Texans", color="00143f", alt_color="c41230")
        # colts = Team(team_name="Colts", color="003b75", alt_color="ffffff")
        # titans = Team(team_name="Titans", color="4b92db", alt_color="002a5c")
        # db.session.add_all([jaguars, texans, colts, titans])
        # db.session.commit()

        # # NFC East
        # giants = Team(team_name="Giants", color="003c7f", alt_color="c9243f")
        # cowboys = Team(team_name="Cowboys", color="002a5c", alt_color="b0b7bc")
        # eagles = Team(team_name="Eagles", color="06424d", alt_color="000000")
        # commanders = Team(team_name="Commanders", color="5a1414", alt_color="ffb612")
        # db.session.add_all([giants, cowboys, eagles, commanders])
        # db.session.commit()

        # # NFC North
        # packers = Team(team_name="Packers", color="204e32", alt_color="ffb612")
        # bears = Team(team_name="Bears", color="0b1c3a", alt_color="e64100")
        # vikings = Team(team_name="Vikings", color="4f2683", alt_color="ffc62f")
        # lions = Team(team_name="Lions", color="0076b6", alt_color="bbbbbb")
        # db.session.add_all([packers, bears, vikings, lions])
        # db.session.commit()

        # # NFC West
        # niners = Team(team_name="49ers", color="aa0000", alt_color="b3995d")
        # seahawks = Team(team_name="Seahawks", color="002a5c", alt_color="69be28")
        # rams = Team(team_name="Rams", color="003594", alt_color="ffd100")
        # cardinals = Team(team_name="Cardinals", color="a4113e", alt_color="ffffff")
        # db.session.add_all([niners, seahawks, rams, cardinals])
        # db.session.commit()

        # # NFC South
        # panthers = Team(team_name="Panthers", color="0085ca", alt_color="000000")
        # buccaneers = Team(team_name="Buccaneers", color="bd1c36", alt_color="3e3a35")
        # saints = Team(team_name="Saints", color="d3bc8d", alt_color="000000")
        # falcons = Team(team_name="Falcons", color="a71930", alt_color="000000")
        # db.session.add_all([panthers, buccaneers, saints, falcons])
        # db.session.commit()
