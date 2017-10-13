package org.gcube.portlets.admin.accountingmanager.client.graphics;

/**
 * 
 * Basic Vector 2D
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class Vector2D {

	public static Vector2D Vector2DNull = new Vector2D(0, 0);

	protected double x;
	protected double y;

	public Vector2D() {
		x = y = 0.0;
	}

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	// Compute magnitude of vector ....
	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	// Sum of two vectors ....
	public Vector2D add(Vector2D v) {
		Vector2D v1 = new Vector2D(this.x + v.x, this.y + v.y);
		return v1;
	}

	// Add scalar
	public Vector2D add(double s) {
		Vector2D v1 = new Vector2D(this.x + s, this.y + s);
		return v1;
	}

	// Subtract vector v1 from v .....
	public Vector2D sub(Vector2D v) {
		Vector2D v1 = new Vector2D(this.x - v.x, this.y - v.y);
		return v1;
	}

	// Subtract scalar
	public Vector2D sub(double s) {
		Vector2D v2 = new Vector2D(this.x - s, this.y - s);
		return v2;
	}

	// Scale vector by a constant ...
	public Vector2D scale(double scaleFactor) {
		Vector2D v2 = new Vector2D(this.x * scaleFactor, this.y * scaleFactor);
		return v2;
	}

	// Normalize a vectors length....
	public Vector2D normalize() {
		Vector2D v2 = new Vector2D();

		double length = Math.sqrt(this.x * this.x + this.y * this.y);
		if (length != 0) {
			v2.x = this.x / length;
			v2.y = this.y / length;
		}

		return v2;
	}

	// Dot product of two vectors .....
	public double dot(Vector2D v) {
		return this.x * v.x + this.y * v.y;
	}

	// Multiply
	public Vector2D multiply(Vector2D v) {
		Vector2D v1 = new Vector2D(this.x * v.getX(), this.y * v.getY());
		return v1;
	}

	public Vector2D multiply(double s) {
		Vector2D v1 = new Vector2D(this.x * s, this.y * s);
		return v1;
	}

	// Divide
	public Vector2D divide(Vector2D v) {
		Vector2D v1 = new Vector2D(this.x / v.getX(), this.y / v.getY());
		return v1;
	}

	public Vector2D divide(double s) {
		if (s != 0) {
			Vector2D v1 = new Vector2D(this.x / s, this.y / s);
			return v1;
		} else {
			return Vector2DNull;
		}

	}

	// Distance To Squared
	public double distanceToSquared(Vector2D v) {
		double dx = this.x - v.x, dy = this.y - v.y;
		return dx * dx + dy * dy;

	}

	// Distance
	public double distance(Vector2D v) {
		return Math.sqrt(this.distanceToSquared(v));
	}

	// Negate
	public Vector2D negate() {
		Vector2D v1 = new Vector2D(-x, -y);
		return v1;
	}

	public boolean equals(Vector2D v) {
		return v.getX() == x && v.getY() == y;
	}

	public Vector2D copy() {
		return new Vector2D(x, y);
	}

	// Floor
	public Vector2D floor() {
		Vector2D v1 = new Vector2D(Math.floor(this.x), Math.floor(this.y));
		return v1;
	}

	// Ceil
	public Vector2D ceil() {
		Vector2D v1 = new Vector2D(Math.ceil(this.x), Math.ceil(this.y));
		return v1;
	}

	// Round
	public Vector2D round() {
		Vector2D v1 = new Vector2D(Math.round(this.x), Math.round(this.y));
		return v1;
	}

	// Round To Zero
	public Vector2D roundToZero() {
		double dx=x<0?Math.ceil( this.x ):Math.floor( this.x );
		double dy=y<0?Math.ceil( this.y ):Math.floor( this.y );
		Vector2D v1 = new Vector2D(dx,dy);
		return v1;
	}

	@Override
	public String toString() {
		return "Vector2D [x=" + x + ", y=" + y + "]";
	}

}
