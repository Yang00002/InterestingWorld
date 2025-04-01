package org.yang.iw.api.java;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

public class ClassLoader
{
	private static Set<String> MIXIN_SET = new ObjectOpenHashSet<>();

	public static void registerMixinPackage(String packageName)
	{
		MIXIN_SET.add(packageName.replaceAll("\\.", "/"));
	}

	private static void findClasses(File directory, String packageName) throws ClassNotFoundException
	{
		if (!directory.exists()) return;
		if (MIXIN_SET.contains(packageName)) return;
		if (packageName.equals("org/yang/iw/client/mixin/mixin")) return;
		if (packageName.equals("org/yang/iw/mixin/mixin")) return;
		File[] files = directory.listFiles();
		if (files != null)
		{
			for (File file : files)
			{
				if (file.isDirectory())
				{
					findClasses(file, packageName + "/" + file.getName());
				}
				else if (file.getName().endsWith(".class"))
				{
					Class.forName(packageName.replaceAll("/", ".") + '.' +
								  file.getName().substring(0, file.getName().length() - 6));
				}
			}
		}
	}

	public static boolean loadPackage(String packagePath)
	{
		try
		{
			packagePath = packagePath.replaceAll("\\.", "/");
			Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);
			while (resources.hasMoreElements())
			{
				URL resource = resources.nextElement();
				File directory = new File(resource.getFile());
				if (directory.exists())
				{
					findClasses(directory, packagePath);
				}
			}
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}
}
