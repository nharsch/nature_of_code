from pvector import PVector

class Mover(object):

    def __init__(self,
                 location=PVector(0,0),
                 velocity=PVector(0,0),
                 acceleration=PVector(0,0),
                 topspeed=10):
        self.location = location
        self.velocity = velocity
        self.acceleration = acceleration
        self.topspeed = topspeed

    def update(self):
        mouse = PVector(mouseX, mouseY)
        dir = mouse.sub(self.location)
        dir = dir.normalize()
        dir = dir.mult(0.5)

        self.acceleration = dir

        self.velocity = self.velocity.add(self.acceleration).limit(self.topspeed)
        self.location = self.location.add(self.velocity)

    def display(self):
        stroke(0)
        fill(175)
        ellipse(self.location.x, self.location.y, 16, 16)

    def checkEdges(self):
        if self.location.x > width:
            self.location.x = 0
        elif self.location.x < 0:
            self.location.x = width

        if self.location.y > height:
            self.location.y = 0
        elif self.location.y < 0:
            self.location.y = height

    def __repr__(self):
        return 'Mover {} {}'.format(self.location, self.velocity)
