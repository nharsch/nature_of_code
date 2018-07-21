class Ball(object):
    def __init__(self, location, velocity):
        self.location = location
        self.velocity = velocity

    def update(self):
        self.location = self.location.add(self.velocity)

        if self.location.x > width or self.location.x < 0:
            self.velocity.x = self.velocity.x * -1
        if self.location.y > height or self.location.y < 0:
            self.velocity.y = self.velocity.y * -1

        stroke(0)
        fill(175)
        ellipse(self.location.x, self.location.y, 16, 16)

        print(width, height, self.location)



