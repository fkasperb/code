public class Text{
		public int x;
		public int y;
		public int xSize;
		public int ySize;
		public int spacing;
		public String text;
		public Text(String text, int x, int y, int xSize, int ySize, int spacing) {
			this.x = x;
			this.y = y;
			this.xSize = xSize;
			this.ySize = ySize;
			this.text = text.toUpperCase();
			this.spacing = spacing;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o == null) return false;
			if(!(o instanceof Text)) return false;
			Text t = (Text) o;
			return this.text.equals(t.text) && this.x == t.x && this.y == t.y && this.xSize == t.xSize && this.ySize == t.ySize && this.spacing == t.spacing;
		}
		
		public void setText(Object o) {
			if(o instanceof Integer) {
				text = Integer.toString((int) o);
			}else if(o instanceof String) {
				text = (String) o;
			}
		}
		
		public Text copy() {
			return new Text(this.text, this.x, this.y, this.xSize, this.ySize, this.spacing);
		}
	}
