package net.nillus.waldkorn.catalogue;

import net.nillus.waldkorn.items.ItemDefinition;

/**
 * CatalogueArticle represents an article in the Catalogue.\r
 * CatalogueArticles are unique in the Catalogue by their 'article ID'.
 * 
 * @author Nillus
 */
public class CatalogueArticle
{
	/**
	 * The purchase code of this article. (barcode)
	 */
	public String id;
	
	/**
	 * The price of this article in Credits.
	 */
	public int price;
	
	/**
	 * The section code of this article.
	 */
	public String section;
	
	/**
	 * The actual ItemDefinition representing the item this article 'ships'.
	 */
	private ItemDefinition m_innerItem;
	
	/**
	 * Returns the ItemDefinition of the inner item in this article.
	 */
	public ItemDefinition getItem()
	{
		return m_innerItem;
	}

	public void setInnerItem(ItemDefinition definition)
	{
		m_innerItem = definition;
	}
}
