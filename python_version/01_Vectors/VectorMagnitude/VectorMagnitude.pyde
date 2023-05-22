from pvector import PVector
from ball import Ball
from mover import Mover


def setup():
    size(650, 350)
    background(255)
    # global ball
    # ball = Ball(PVector(100,200), PVector(2.5,5))

    global movers
    movers = [Mover(location=PVector(i*1,i*2)) for i in range(10)]

def draw():
    background(255)

    # ball.update()

    print(movers)
    map(lambda m: m.update(), movers)
    # mover.checkEdges()
    map(lambda m: m.display(), movers)

    # mouse = PVector(mouseX, mouseY)
    # center = PVector(width/2, height/2)
    # mouse = mouse.sub(center)
    #
    # mouse = mouse.normalize()
    # mouse = mouse.mult(50)
    # translate(width/2,height/2)
    # line(0,0, mouse.x, mouse.y)
    #
    #
