import pytest
import os
import sys

project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), os.pardir))
sys.path.append(project_root)

from app import app
from server.extensions import db
from server.models import User, Year, Team, DraftPick, UserDraftPick


class TestDraftAssignments:
    """Tests related to draft assignments"""

    def test_draft_assignments_to_user(self):
        pass

    def test_team_assignments_to_user(self):
        pass
