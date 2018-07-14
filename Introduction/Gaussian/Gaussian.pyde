from random import gauss


def setup():
    size(640,240)

def draw():
    x = gauss(320,60)
    y = gauss(120,60)

    noStroke()
    fill(255,10)
    ellipse(x,y,16,16)

