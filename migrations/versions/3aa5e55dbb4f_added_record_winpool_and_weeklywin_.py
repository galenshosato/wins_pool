"""Added Record, WinPool, and WeeklyWin models

Revision ID: 3aa5e55dbb4f
Revises: 4ad5ca2155f4
Create Date: 2023-12-26 13:44:58.480417

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '3aa5e55dbb4f'
down_revision = '4ad5ca2155f4'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('records',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('team_id', sa.Integer(), nullable=True),
    sa.Column('year_id', sa.Integer(), nullable=True),
    sa.Column('wins', sa.Integer(), nullable=True),
    sa.Column('losses', sa.Integer(), nullable=True),
    sa.Column('ties', sa.Integer(), nullable=True),
    sa.ForeignKeyConstraint(['team_id'], ['teams.id'], name=op.f('fk_records_team_id_teams')),
    sa.ForeignKeyConstraint(['year_id'], ['years.id'], name=op.f('fk_records_year_id_years')),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('weekly_wins',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('user_id', sa.Integer(), nullable=True),
    sa.Column('year_id', sa.Integer(), nullable=True),
    sa.Column('week_id', sa.Integer(), nullable=True),
    sa.Column('wins', sa.Integer(), nullable=True),
    sa.ForeignKeyConstraint(['user_id'], ['users.id'], name=op.f('fk_weekly_wins_user_id_users')),
    sa.ForeignKeyConstraint(['week_id'], ['weeks.id'], name=op.f('fk_weekly_wins_week_id_weeks')),
    sa.ForeignKeyConstraint(['year_id'], ['years.id'], name=op.f('fk_weekly_wins_year_id_years')),
    sa.PrimaryKeyConstraint('id')
    )
    op.create_table('wins_pool',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('user_draft_pick_id', sa.Integer(), nullable=True),
    sa.Column('user_id', sa.Integer(), nullable=True),
    sa.Column('year_id', sa.Integer(), nullable=True),
    sa.Column('total_wins', sa.Integer(), nullable=True),
    sa.ForeignKeyConstraint(['user_draft_pick_id'], ['user_draft_picks.id'], name=op.f('fk_wins_pool_user_draft_pick_id_user_draft_picks')),
    sa.ForeignKeyConstraint(['user_id'], ['users.id'], name=op.f('fk_wins_pool_user_id_users')),
    sa.ForeignKeyConstraint(['year_id'], ['years.id'], name=op.f('fk_wins_pool_year_id_years')),
    sa.PrimaryKeyConstraint('id')
    )
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('wins_pool')
    op.drop_table('weekly_wins')
    op.drop_table('records')
    # ### end Alembic commands ###
