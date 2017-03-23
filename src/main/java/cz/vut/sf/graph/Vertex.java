package cz.vut.sf.graph;


public class Vertex {

	public final int id;
	
	public Vertex(int id) {
		this.id = id;
	}

	public Vertex getById(int lookingId){
		if(this.id == lookingId){
			return this;
		}
		return null;
	}
	
	public Vertex(Vertex source){
		this.id = source.id;
	}

	@Override
	public String toString() {
		return "V" + id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
