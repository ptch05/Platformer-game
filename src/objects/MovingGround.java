package objects;

import org.jbox2d.common.Vec2;

import city.cs.engine.*;

public class MovingGround extends StaticBody implements StepListener{
  private static final Shape movingGroundShape = new BoxShape(9f, 3f);
  private static final BodyImage movingGroundImage = new BodyImage("./assets/images/level-data/level2/moving-ground.png", 11.5f);
  
  private Vec2 startPosition;
  private float left, right;
  private float delta;

  public MovingGround(World world) {
    super(world, movingGroundShape);
    startPosition = this.getPosition();
    addImage(movingGroundImage);
    left = startPosition.x;
    right = startPosition.x+40;
    delta=0.08f;
    world.addStepListener(this);
  }

  @Override
  public void postStep(StepEvent stepEvent) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'postStep'");
  }

  @Override
    public void preStep(StepEvent stepEvent) {
        if (getPosition().x < left){
            this.setPosition(startPosition);
            delta*=-1;
        }
        if (getPosition().x > right){
            delta*=-1;
        }
        this.setPosition(new Vec2(this.getPosition().x+delta, this.getPosition().y));
  }
}
