class Mover(object):

    def __init__(self,
                 location=PVector(0,0),
                 velocity=PVector(0,0),
                 acceleration=PVector(0,0),
                 topspeed=10):
        self.location = location
        self.velocity = velocity
        self.acceleration = acceleration
        self.mass = 10
        self.topspeed = topspeed

    def update(self):
        self.velocity += self.acceleration
        self.location += self.velocity
        self.acceleration *= 0

    def display(self):
        stroke(0)
        fill(175)
        ellipse(self.location.x, self.location.y, 16, 16)

    def checkEdges(self):
        if self.location.x > width:
            self.location.x = width
            self.velocity.x *= -1
        elif self.location.x < 0:
            self.location.x = 0
            self.velocity.x *= -1

        if self.location.y > height:
            self.location.y = height
            self.velocity.y *= -1

    def applyForce(self, force):
        force = force / self.mass
        self.acceleration += force

    def __repr__(self):
        return 'Mover {} {}'.format(self.location, self.velocity)
