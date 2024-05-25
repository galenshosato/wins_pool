"""Added isActive to Week and Year

Revision ID: eced0d7fbc7f
Revises: 403a41eaa8c8
Create Date: 2024-01-11 20:46:53.702705

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'eced0d7fbc7f'
down_revision = '403a41eaa8c8'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('weeks', schema=None) as batch_op:
        batch_op.add_column(sa.Column('isActive', sa.Boolean(), nullable=True))

    with op.batch_alter_table('years', schema=None) as batch_op:
        batch_op.add_column(sa.Column('isActive', sa.Boolean(), nullable=True))

    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('years', schema=None) as batch_op:
        batch_op.drop_column('isActive')

    with op.batch_alter_table('weeks', schema=None) as batch_op:
        batch_op.drop_column('isActive')

    # ### end Alembic commands ###
