from random import gauss


class Walker(object):
    def __init__(self):
        self.x = width/2
        self.y = height/2

    def step(self):
        x = gauss(0,1)
        y = gauss(0,1)
        self.x += x
        self.y += y

    def render(self):
        stroke(0)
        point(self.x, self.y)
