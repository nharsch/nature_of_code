from mover import Mover

def setup():
    size(650, 350)
    background(255)
    # global ball
    # ball = Ball(PVector(100,200), PVector(2.5,5))

    global mover, wind, gravity, forces
    mover = Mover(location=PVector(width/2, height/2),
                  velocity=PVector(0,0))
    gravity = PVector(0, 1*mover.mass) # scale gravity to obj mass


def draw():
    background(255)
    if mousePressed:
        wind = PVector(1, -5)
    else:
        wind = PVector(0, 0)

    mover.applyForce(gravity)
    mover.applyForce(wind)
    mover.checkEdges()
    mover.update()
    mover.display()


