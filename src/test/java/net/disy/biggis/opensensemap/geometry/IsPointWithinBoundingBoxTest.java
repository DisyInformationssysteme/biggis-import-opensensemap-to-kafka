package net.disy.biggis.opensensemap.geometry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geojson.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IsPointWithinBoundingBoxTest {

  @Test
  public void pointWithinBox() throws Exception {
    IsPointWithinBoundingBox predicate = new IsPointWithinBoundingBox(0, 5, 10, 15);
    assertTrue("Point should be within bounding box", predicate.test(new Point(12, 2)));
  }

  @Test
  public void pointOutsideBox() throws Exception {
    IsPointWithinBoundingBox predicate = new IsPointWithinBoundingBox(0, 5, 10, 15);
    assertFalse("Point should be outside bounding box", predicate.test(new Point(20, 20)));
  }

  @Test
  public void pointWithinLatitudeOutsideLongitude() throws Exception {
    IsPointWithinBoundingBox predicate = new IsPointWithinBoundingBox(0, 5, 10, 15);
    assertFalse("Point should be outside bounding box", predicate.test(new Point(20, 12)));
  }

  @Test
  public void pointOutsideLatitudeWithinLongitude() throws Exception {
    IsPointWithinBoundingBox predicate = new IsPointWithinBoundingBox(0, 5, 10, 15);
    assertFalse("Point should be outside bounding box", predicate.test(new Point(2, 20)));
  }

  @Test
  public void pointsOnCorners() throws Exception {
    IsPointWithinBoundingBox predicate = new IsPointWithinBoundingBox(0, 5, 10, 15);
    assertTrue("Point should be within bounding box", predicate.test(new Point(10, 0)));
    assertTrue("Point should be within bounding box", predicate.test(new Point(10, 5)));
    assertTrue("Point should be within bounding box", predicate.test(new Point(15, 0)));
    assertTrue("Point should be within bounding box", predicate.test(new Point(15, 5)));
  }

}
