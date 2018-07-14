from random import gauss


class Walker(object):
    def __init__(self):
        self.x = width/2
        self.y = height/2

    def step(self):
        r = montecarlo()
        if (r < 0.01):
            x = random(-100,100)
            y = random(-100,100)
        else:
            x = random(-1,1)
            y = random(-1,1)
        self.x += x
        self.y += y

    def render(self):
        stroke(0)
        point(self.x, self.y)


def montecarlo():
    while True:
        r1 = random(1)
        probability = r1
        r2 = random(1)
        if r2 < probability:
            return r1

