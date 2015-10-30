/*
 * Copyright (c) 2007, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.pdf.pd;

import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNull;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.font.PDFont;

import java.util.HashMap;
import java.util.Map;

/**
 * The implementation of a resource dictionary.
 * <p>
 * <p>
 * The resource dictionary holds the references to indirect objects that are use
 * within a {@link CSContent}. From the {@link CSContent}, these objects are
 * referenced using the key under which they are stored in the dictionary.
 * </p>
 */
public class PDResources extends PDObject {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDObject.MetaClass {
        protected MetaClass(Class paramInstanceClass) {
            super(paramInstanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDResources(object);
        }
    }

    public static final COSName CN_RT_ColorSpace = COSName.constant("ColorSpace"); //$NON-NLS-1$

    public static final COSName CN_RT_Encoding = COSName.constant("Encoding"); //$NON-NLS-1$

    /**
     * the resource types in a resource dictionaries
     */
    public static final COSName CN_RT_ExtGState = COSName.constant("ExtGState"); //$NON-NLS-1$

    public static final COSName CN_RT_Font = COSName.constant("Font"); //$NON-NLS-1$

    public static final COSName CN_RT_Pattern = COSName.constant("Pattern"); //$NON-NLS-1$

    public static final COSName CN_RT_ProcSet = COSName.constant("ProcSet"); //$NON-NLS-1$

    public static final COSName CN_RT_Properties = COSName.constant("Properties"); //$NON-NLS-1$

    public static final COSName CN_RT_Shading = COSName.constant("Shading"); //$NON-NLS-1$

    public static final COSName CN_RT_XObject = COSName.constant("XObject"); //$NON-NLS-1$

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    private static Map resourceNamePrefixes = new HashMap();

    static {
        resourceNamePrefixes.put(CN_RT_ColorSpace, "CS"); //$NON-NLS-1$
        resourceNamePrefixes.put(CN_RT_ExtGState, "GS"); //$NON-NLS-1$
        resourceNamePrefixes.put(CN_RT_Font, "F"); //$NON-NLS-1$
        resourceNamePrefixes.put(CN_RT_Pattern, "P"); //$NON-NLS-1$
        resourceNamePrefixes.put(CN_RT_Shading, "S"); //$NON-NLS-1$
        resourceNamePrefixes.put(CN_RT_XObject, "X"); //$NON-NLS-1$
    }

    private Map currentCounters = new HashMap();

    /**
     * a cached representation of the resources
     */
    private Map cachedResources = new HashMap();

    /**
     * Create a resource dictionary
     *
     * @param object The COS Object implementing the dictionary
     */
    protected PDResources(COSObject object) {
        super(object);
    }

    public void addColorSpaceResource(COSName name, PDColorSpace colorSpace) {
        addResource(CN_RT_ColorSpace, name, colorSpace);
    }

    public void addExtGStateResource(COSName name, PDExtGState extGState) {
        addResource(CN_RT_ExtGState, name, extGState);
    }

    public void addFontResource(COSName name, PDFont font) {
        addResource(CN_RT_Font, name, font);
    }

    public void addPatternResource(COSName name, PDPattern pattern) {
        addResource(CN_RT_Pattern, name, pattern);
    }

    public void addResource(COSName resourceType, COSName name, PDObject object) {
        COSDictionary dict = cosGetResources(resourceType);
        if (dict == null) {
            dict = cosInitResources(resourceType);
        }
        dict.put(name, object.cosGetObject());
    }

    public void addShadingResource(COSName name, PDShading shading) {
        addResource(CN_RT_Shading, name, shading);
    }

    public void addXObjectResource(COSName name, PDXObject object) {
        addResource(CN_RT_XObject, name, object);
    }

    public void cosAddResource(COSName resourceType, COSName resourceName, COSObject resource) {
        COSDictionary dict = cosGetResources(resourceType);
        if (dict == null) {
            dict = cosInitResources(resourceType);
        }
        dict.put(resourceName, resource);
    }

    public COSName cosCreateResource(COSName resourceType, COSObject resource) {
        COSDictionary resources = cosGetResources(resourceType);
        if (resources == null) {
            resources = cosInitResources(resourceType);
        }
        COSName key = resources.keyOf(resource).asName();
        if (key == null) {
            key = createResourceName(resourceType);
            resources.put(key, resource);
        }
        return key;
    }

    public COSObject cosGetResource(COSName resourceType, COSName resourceName) {
        COSDictionary resources = cosGetResources(resourceType);
        if (resources == null) {
            return COSNull.NULL;
        }
        return resources.get(resourceName);
    }

    public COSDictionary cosGetResources(COSName resourceType) {
        COSDictionary resources = (COSDictionary) cachedResources.get(resourceType);
        if (resources == null) {
            resources = cosGetField(resourceType).asDictionary();
            cachedResources.put(resourceType, resources);
        }
        return resources;
    }

    public COSDictionary cosInitResources(COSName resourceType) {
        COSDictionary resource = COSDictionary.create();
        cosSetField(resourceType, resource);
        return resource;
    }

    public void cosSetResources(COSName resourceType, COSDictionary resource) {
        cosSetField(resourceType, resource);
    }

    /**
     * Return the name of a ColorSpace resource within the resource dictionary.
     * <p>
     * <p>
     * A new entry is created if the resource is not found.
     * </p>
     *
     * @param colorSpace The colorSpace to lookup.
     * @return the name of a ColorSpace resource within the resource dictionary.
     */
    public COSName createColorSpaceResource(PDColorSpace colorSpace) {
        return createResource(CN_RT_ColorSpace, colorSpace);
    }

    /**
     * Return the name of a ExtGState resource within the resource dictionary.
     * <p>
     * <p>
     * A new entry is created if the resource is not found.
     * </p>
     *
     * @param gstate The gstate to lookup.
     * @return the name of a ExtGState resource within the resource dictionary.
     */
    public COSName createExtGStateResource(PDExtGState gstate) {
        return createResource(CN_RT_ExtGState, gstate);
    }

    /**
     * Return the name of the font resource within this resource dictionary.
     * <p>
     * <p>
     * A new entry is created if the resource is not found.
     * </p>
     *
     * @param font The font whose name is looked up.
     * @return the name of the font resource within this resource dictionary.
     */
    public COSName createFontResource(PDFont font) {
        return createResource(CN_RT_Font, font);
    }

    /**
     * Return the name of a new resource of selected resource type within the
     * resource dictionary.
     * <p>
     * <p>
     * A new entry is created if the resource is not found.
     * </p>
     *
     * @param object The resource to lookup.
     * @return The name of a resource within the resource dictionary.
     */
    public COSName createResource(COSName resourceType, PDObject object) {
        return cosCreateResource(resourceType, object.cosGetObject());
    }

    protected COSName createResourceName(COSName resourceType) {
        Integer count = (Integer) currentCounters.get(resourceType);
        if (count == null) {
            count = Integer.valueOf(0);
        }
        String prefix = (String) resourceNamePrefixes.get(resourceType);
        while (true) {
            count = Integer.valueOf(count.intValue() + 1);
            COSName newName = COSName.create((prefix + count).getBytes());
            COSObject object = cosGetResource(resourceType, newName);
            if (object.isNull()) {
                currentCounters.put(resourceType, count);
                return newName;
            }
        }
    }

    /**
     * Return the name of a shading resource within the resource dictionary.
     * <p>
     * <p>
     * A new entry is created if the resource is not found.
     * </p>
     *
     * @param shading The shading to lookup.
     * @return The name of the resource within the resource dictionary.
     */
    public COSName createShadingResource(PDShading shading) {
        return createResource(CN_RT_Shading, shading);
    }

    /**
     * Return the name of a XObject resource within the resource dictionary.
     * <p>
     * <p>
     * A new entry is created if the resource is not found.
     * </p>
     *
     * @param xobject The object to lookup.
     * @return the name of a XObject resource within the resource dictionary.
     */
    public COSName createXObjectResource(PDXObject xobject) {
        return createResource(CN_RT_XObject, xobject);
    }

    /**
     * A named ColorSpace resource from the resource dictionary.
     *
     * @param name The name of the ColorSpace resource.
     * @return A named ColorSpace resource from the resource dictionary.
     */
    public PDColorSpace getColorSpaceResource(COSName name) {
        return (PDColorSpace) getResource(CN_RT_ColorSpace, PDColorSpace.META, name);
    }

    /**
     * Return a named ExtGState resource from the resource dictionary.
     *
     * @param name The name of the ExtGState resource.
     * @return a named ExtGState resource from the resource dictionary.
     */
    public PDExtGState getExtGStateResource(COSName name) {
        return (PDExtGState) getResource(CN_RT_ExtGState, PDExtGState.META, name);
    }

    /**
     * Return a named font resource.
     *
     * @param name The name of the font resource
     * @return A named font resource from within the resource dictionary.
     */
    public PDFont getFontResource(COSName name) {
        return (PDFont) getResource(CN_RT_Font, PDFont.META, name);
    }

    /**
     * Return a named Pattern resource from the resource dictionary.
     *
     * @param name The name of the Pattern resource.
     * @return a named Pattern resource from the resource dictionary.
     */
    public PDPattern getPatternResource(COSName name) {
        return (PDPattern) getResource(CN_RT_Pattern, PDPattern.META, name);
    }

    /**
     * Return a named resource from the resource dictionary.
     *
     * @param name The name of the resource.
     * @return a named resource from the resource dictionary.
     */
    public PDObject getResource(COSName resourceType, PDObject.MetaClass metaClass, COSName name) {
        COSDictionary resources = cosGetResources(resourceType);
        if (resources == null) {
            return null;
        }
        return (PDObject) metaClass.createFromCos(resources.get(name));
    }

    /**
     * Return a named Pattern resource from the resource dictionary.
     *
     * @param name The name of the Pattern resource.
     * @return a named Pattern resource from the resource dictionary.
     */
    public PDShading getShadingResource(COSName name) {
        return (PDShading) getResource(CN_RT_Shading, PDShading.META, name);
    }

    /**
     * Return a named XObject resource from the resource dictionary.
     *
     * @param name The name of the XObject resource.
     * @return a named XObject resource from the resource dictionary.
     */
    public PDXObject getXObjectResource(COSName name) {
        return (PDXObject) getResource(CN_RT_XObject, PDXObject.META, name);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSBasedObject#invalidateCaches()
     */
    @Override
    public void invalidateCaches() {
        super.invalidateCaches();
        cachedResources.clear();
    }
}
