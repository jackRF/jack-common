public class ${name}{
<#list properties as property>
	/**
	 * ${property.comment}
	 */
	private ${property.type} ${property.name};
	
<#list>
}