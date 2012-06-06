package org.jetbrains.jps.model;

import org.jetbrains.jps.model.impl.JpsProjectImpl;
import org.jetbrains.jps.model.java.JpsJavaLibraryType;
import org.jetbrains.jps.model.java.JpsJavaModuleType;
import org.jetbrains.jps.model.java.JavaSourceRootProperties;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.library.JpsLibrary;
import org.jetbrains.jps.model.module.*;
import org.jetbrains.jps.model.module.impl.JpsModuleReferenceImpl;

import java.util.List;

/**
 * @author nik
 */
public class JpsModuleTest extends JpsModelTestCase {
  public void testAddSourceRoot() {
    final JpsModule module = myModel.getProject().addModule(JpsJavaModuleType.INSTANCE, "m");
    final JpsModuleSourceRoot sourceRoot = module.addSourceRoot(JavaSourceRootType.SOURCE, "file://url", new JavaSourceRootProperties("com.xxx"));

    assertSameElements(myDispatcher.retrieveAdded(JpsModule.class), module);
    assertSameElements(myDispatcher.retrieveAdded(JpsModuleSourceRoot.class), sourceRoot);

    final JpsModuleSourceRoot root = assertOneElement(module.getSourceRoots());
    assertEquals("file://url", root.getUrl());
    final JavaSourceRootProperties properties = root.getProperties(JavaSourceRootType.SOURCE);
    assertNotNull(properties);
    assertEquals("com.xxx", properties.getPackagePrefix());
  }

  public void testModifiableModel() {
    final JpsModule module = myModel.getProject().addModule(JpsJavaModuleType.INSTANCE, "m");
    final JpsModuleSourceRoot root0 = module.addSourceRoot(JavaSourceRootType.SOURCE, "url1");
    myDispatcher.clear();

    final JpsModel modifiableModel = myModel.createModifiableModel(new TestJpsEventDispatcher());
    final JpsModule modifiableModule = assertOneElement(modifiableModel.getProject().getModules());
    modifiableModule.addSourceRoot(JavaSourceRootType.TEST_SOURCE, "url2");
    modifiableModel.commit();

    assertEmpty(myDispatcher.retrieveAdded(JpsModule.class));
    assertEmpty(myDispatcher.retrieveRemoved(JpsModule.class));

    final List<? extends JpsModuleSourceRoot> roots = module.getSourceRoots();
    assertEquals(2, roots.size());
    assertSame(root0, roots.get(0));
    final JpsModuleSourceRoot root1 = roots.get(1);
    assertEquals("url2", root1.getUrl());
    assertOrderedEquals(myDispatcher.retrieveAdded(JpsModuleSourceRoot.class), root1);
    assertEmpty(myDispatcher.retrieveChanged(JpsModuleSourceRoot.class));
  }

  public void testAddDependency() {
    final JpsModule module = myModel.getProject().addModule(JpsJavaModuleType.INSTANCE, "m");
    final JpsLibrary library = myModel.getProject().addLibrary(JpsJavaLibraryType.INSTANCE, "l");
    final JpsModule dep = myModel.getProject().addModule(JpsJavaModuleType.INSTANCE, "dep");
    module.getDependenciesList().addLibraryDependency(library);
    module.getDependenciesList().addModuleDependency(dep);

    final List<? extends JpsDependencyElement> dependencies = module.getDependenciesList().getDependencies();
    assertEquals(2, dependencies.size());
    assertSame(library, assertInstanceOf(dependencies.get(0), JpsLibraryDependency.class).getLibraryReference().resolve());
    assertSame(dep, assertInstanceOf(dependencies.get(1), JpsModuleDependency.class).getModuleReference().resolve());
  }

  public void testCreateReferenceByModule() {
    final JpsModule module = myModel.getProject().addModule(JpsJavaModuleType.INSTANCE, "m");
    final JpsModuleReference reference = module.createReference(myModel.getProject());
    assertEquals("m", reference.getModuleName());
    assertSame(module, reference.resolve());
  }

  public void testCreateReferenceByName() {
    final JpsProjectImpl project = myModel.getProject();
    final JpsModuleReference reference = new JpsModuleReferenceImpl(myModel, "m", myDispatcher, project);
    assertEquals("m", reference.getModuleName());
    assertNull(reference.resolve());

    final JpsModule module = project.addModule(JpsJavaModuleType.INSTANCE, "m");
    assertSame(module, reference.resolve());
  }
}
