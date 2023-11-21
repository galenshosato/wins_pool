import pytest
import requests
import sys

sys.path.append("/home/galensato/Development/code/coding-projects/wins_pool")

from app import app
from server.extensions import db
from server.models import User, Year, Team, DraftPick, UserDraftPick


class TestCRUDUser:
    def sample_data():
        pass
