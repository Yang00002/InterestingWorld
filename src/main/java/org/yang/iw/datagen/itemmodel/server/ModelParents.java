package org.yang.iw.datagen.itemmodel.server;

import net.minecraft.client.data.Model;
import net.minecraft.client.data.Models;

public enum ModelParents
{
	GENERATED, HANDHELD, HANDHELD_ROD;

	public Model toClientModel()
	{
		Model m = null;
		switch (this)
		{
			case GENERATED -> m = Models.GENERATED;
			case HANDHELD -> m = Models.HANDHELD;
			case HANDHELD_ROD -> m = Models.HANDHELD_ROD;
		}
		return m;
	}
}
