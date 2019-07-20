/**
 * 
 */
package cn.rongcapital.caas.generator;

/**
 * @author zhaohai
 *
 */
public interface IdGenerator {

	/**
	 * Generate unique id
	 * 
	 * @return type the id type
	 */
	public String generate(IdType type);

	/**
	 * the ID type
	 * 
	 * @author shangchunming@rongcapital.cn
	 *
	 */
	public static enum IdType {

		/**
		 * the user Id
		 */
		USER("USER"),

		/**
		 * the Operation Id
		 */
		OPERATION("OPERATION"),

		/**
		 * the subject Id
		 */
		SUBJECT("SUBJECT"),

		/**
		 * the upload result Id
		 */
        USER_UPLOAD("USER_UPLOAD");

		private final String code;

		private IdType(final String code) {
			this.code = code;
		}

		/**
		 * the get the type code
		 * 
		 * @return the code
		 */
		public String getCode() {
			return this.code;
		}

	}
}
