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
package de.intarsys.pdf.cos;

import de.intarsys.pdf.cds.CDSDate;
import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.attribute.TaggedAttribute;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * The abstract superclass for all objects/data structures that are build on the
 * basic COSObject types.
 * <p>
 * <p>
 * The base {@link COSObject} will represent the state while this wrapper will
 * provide the behavior.
 * <p>
 * <p>
 * The {@link COSBasedObject} and its base {@link COSObject} are always closely
 * related, all changes are immediately reflected in both objects.
 * <p>
 * The {@link COSBasedObject} uses a META framework that ensures identity (you
 * will always get the identical {@link COSBasedObject} for a {@link COSObject}
 * created via META) and defines the lifecycle of the {@link COSBasedObject}.
 * <p>
 * A {@link COSBasedObject} should always be created using
 * {@code META.createNew} or {@code META.createFromCos}.
 * </p>
 * <p>
 * <p>
 * A {@link COSBasedObject} based on a {@link COSDictionary} can use some
 * convenience methods for generic access to its fields. As a convention, filed
 * names are always declared with the associated {@link COSBasedObject} as
 * {@code public static final COSName DK_<name>}.
 * <p>
 * <p>
 * The {@link COSBasedObject} implements {@link IAttributeSupport}. Client code
 * can use this feature to transparently associate objects with objects from
 * client code, for example for caching or client defined relationships.
 */
public abstract class COSBasedObject implements IAttributeSupport, ICOSObjectListener {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends de.intarsys.pdf.cos.MetaClass {
        /**
         * The cached constructor method
         */
        private Constructor constructor;

        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        public COSBasedObject createFromCos(COSObject object) {
            COSBasedObject result = null;
            if ((object != null) && !object.isNull()) {
                if (object instanceof COSCompositeObject) {
                    result = (COSBasedObject) ((COSCompositeObject) object).getAttribute(getRootClass());
                }
                if (result == null) {
                    MetaClass metaClass = doDetermineClass(object);
                    if (metaClass != null) {
                        result = metaClass.doCreateCOSBasedObject(object);
                        if (result != null) {
                            result.initializeFromCos();
                            if (object instanceof COSCompositeObject) {
                                ((COSCompositeObject) object).setAttribute(getRootClass(), result);
                            }
                        }
                    }
                }
            }
            return result;
        }

        public COSBasedObject createNew() {
            COSObject cosObject = doCreateCOSObject();
            if (isIndirect()) {
                cosObject.beIndirect();
            }
            COSBasedObject result = doCreateCOSBasedObject(cosObject);
            result.initializeFromScratch();
            if (cosObject instanceof COSCompositeObject) {
                ((COSCompositeObject) cosObject).setAttribute(getRootClass(), result);
            }
            return result;
        }

        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            try {
                COSBasedObject result;
                synchronized (this) {
                    // lazy access must be synchronized
                    if (constructor == null) {
                        constructor = getInstanceClass().getDeclaredConstructor(COSObject.class);
                        constructor.setAccessible(true);
                    }
                }
                result = (COSBasedObject) constructor.newInstance(object);
                return result;
            } catch (NoSuchMethodException ignored) {
                throw new IllegalStateException("Constructor " //$NON-NLS-1$
                                                + getInstanceClass().getName() + "(COSObject) missing"); //$NON-NLS-1$
            } catch (InstantiationException e) {
                throw new IllegalStateException(getInstanceClass().getName()
                                                + " can not be instantiated ("
                                                + e.getMessage()
                                                + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(getInstanceClass().getName()
                                                + " illegal access ("
                                                + e.getMessage()
                                                + ")"); //$NON-NLS-1$ //$NON-NLS-2$
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(getInstanceClass().getName()
                                                + " invocation target exception("
                                                + e.getMessage()
                                                //$NON-NLS-1$
                                                + ")", e.getCause()); //$NON-NLS-1$
            }
        }

        protected COSObject doCreateCOSObject() {
            return COSDictionary.create();
        }

        protected MetaClass doDetermineClass(COSObject object) {
            return this;
        }

        protected boolean isIndirect() {
            return true;
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    /**
     * This is the base object representing the state.
     * <p>
     * <p>
     * Most of the times the base object is a dictionary. In some cases the
     * complex object is build upon an array (color space) or stream (functions
     * and color spaces). But there is no rule that a complex object is build
     * upon these containers or a {@link COSObject} at all. In certain cases the
     * base object is allowed to be null. This is for example the case with the
     * singleton implementations of the device color spaces.
     * </p>
     */
    private final COSObject object;

    private final IAttributeSupport attributeSupport;

    protected COSBasedObject(COSObject object) {
        super();
        this.object = object;
        if (object != null) {
            object.addObjectListener(this);
        }
        if (object instanceof IAttributeSupport) {
            attributeSupport = (IAttributeSupport) object;
        } else {
            attributeSupport = new AttributeMap();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.cos.ICOSObjectListener#changedSlot(de.intarsys.pdf.cos
     * .COSObject, java.lang.Object, de.intarsys.pdf.cos.COSObject,
     * de.intarsys.pdf.cos.COSObject)
     */
    @Override
    public void changed(COSObject pObject, Object slot, Object oldValue, Object newValue) {
        if (slot != COSObject.SLOT_CONTAINER) {
            invalidateCaches();
        }
    }

    /**
     * Get the base object as a {@link COSArray}.
     * <p>
     * This will throw a {@link ClassCastException} if the base type is not
     * appropriate!
     *
     * @return Get the base object as a {@link COSArray}.
     */
    public COSArray cosGetArray() {
        return (COSArray) object;
    }

    /**
     * Get the base object as a {@link COSDictionary}.
     * <p>
     * This will throw a {@link ClassCastException} if the base type is not
     * appropriate!
     *
     * @return Get the base object as a {@link COSDictionary}.
     */
    public COSDictionary cosGetDict() {
        return (COSDictionary) object;
    }

    /**
     * The {@link COSDocument} for this.
     *
     * @return The {@link COSDocument} for this.
     */
    public COSDocument cosGetDoc() {
        return object.getDoc();
    }

    /**
     * The {@link COSObject} associated with {@code name} in the receiver
     * or {@link COSNull}.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name The {@link COSDictionary} field to read
     * @return The {@link COSObject} associated with {@code name} in the
     * receiver or {@link COSNull}.
     */
    public COSObject cosGetField(COSName name) {
        return cosGetDict().get(name);
    }

    /**
     * The base {@link COSObject} for this.
     *
     * @return The base {@link COSObject} for this.
     */
    public COSObject cosGetObject() {
        return object;
    }

    /**
     * Get the base object as a {@link COSStream}.
     * <p>
     * This will throw a {@link ClassCastException} if the base type is not
     * appropriate!
     *
     * @return Get the base object as a {@link COSStream}.
     */
    public COSStream cosGetStream() {
        return (COSStream) object;
    }

    /**
     * Answer {@code true} if this has a field named {@code name}.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name the field to check
     * @return Answer {@code true} if this has a field named
     * {@code name}.
     */
    public boolean cosHasField(COSName name) {
        return !cosGetDict().get(name).isNull();
    }

    /**
     * Remove a field in this. The previously associated object is returned.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name the field to remove from the receiver
     * @return The previously associated object is returned.
     */
    public COSObject cosRemoveField(COSName name) {
        return cosGetDict().remove(name);
    }

    /**
     * Set a field value in this. The previously associated object is returned.
     * <p>
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name   The field to set
     * @param cosObj The object to set in the field
     * @return The previously associated object is returned.
     */
    public COSObject cosSetField(COSName name, COSObject cosObj) {
        if ((cosObj == null) || cosObj.isNull()) {
            return cosRemoveField(name);
        } else {
            return cosGetDict().put(name, cosObj);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.tools.component.IAttributeSupport#getAttribute(java.lang.
     * Object)
     */
    @Override
    public Object getAttribute(Object key) {
        return attributeSupport.getAttribute(new TaggedAttribute(key, getClass()));
    }

    /**
     * The value of a field within this as a {@code boolean} or the
     * {@code defaultValue} if not found or not a {@link COSBoolean}.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name         The name of the field.
     * @param defaultValue The default value to return if field is not found or not of
     *                     appropriate type.
     * @return The value of a field within this as a {@code boolean}
     */
    public boolean getFieldBoolean(COSName name, boolean defaultValue) {
        COSBoolean value = cosGetField(name).asBoolean();
        if (value == null) {
            return defaultValue;
        }
        return value.booleanValue();
    }

    /**
     * The value of a field within this as a {@link CDSDate} or the
     * {@code defaultValue} if not found or not a {@link COSString}.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name         The name of the field.
     * @param defaultValue The default value to return if field is not found or not of
     *                     appropriate type.
     * @return The value of a field within this as a {@link CDSDate}
     */
    public CDSDate getFieldDate(COSName name, CDSDate defaultValue) {
        COSString value = cosGetField(name).asString();
        if (value == null) {
            return defaultValue;
        }
        return CDSDate.createFromCOS(value);
    }

    /**
     * The value of a field within this as a {@code float} or the
     * {@code defaultValue} if not found or not a {@link COSNumber}.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name         The name of the field.
     * @param defaultValue The default value to return if field is not found or not of
     *                     appropriate type.
     * @return The value of a field within this as a {@code float}
     */
    public float getFieldFixed(COSName name, float defaultValue) {
        COSNumber value = cosGetField(name).asNumber();
        if (value == null) {
            return defaultValue;
        }
        return value.floatValue();
    }

    /**
     * The value of a field within this as a {@code float[]} or the
     * {@code defaultValue} if not found or not a {@link COSArray}.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name         The name of the field.
     * @param defaultValue The default value to return if field is not found or not of
     *                     appropriate type.
     * @return The value of a field within this as a {@code float[]}
     */
    public float[] getFieldFixedArray(COSName name, float[] defaultValue) {
        COSArray array = cosGetField(name).asArray();
        if (array != null) {
            float[] result = new float[array.size()];
            for (int i = 0; i < array.size(); i++) {
                COSNumber fixed = array.get(i).asNumber();
                if (fixed != null) {
                    result[i] = fixed.floatValue();
                } else {
                    // TODO 3 wrong default, maybe restrict
                    result[i] = 0;
                }
            }
            return result;
        }
        return defaultValue;
    }

    /**
     * The value of a field within this as a {@code int} or the
     * {@code defaultValue} if not found or not a {@link COSNumber}.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name         The name of the field.
     * @param defaultValue The default value to return if field is not found or not of
     *                     appropriate type.
     * @return The value of a field within this as a {@code int}
     */
    public int getFieldInt(COSName name, int defaultValue) {
        COSNumber value = cosGetField(name).asNumber();
        if (value == null) {
            return defaultValue;
        }
        return value.intValue();
    }

    /**
     * The value of a field within this as a {@code String} or the
     * {@code defaultValue} if not found or not a {@link COSString}. The
     * String is "expanded" to containn the correct new line characters.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name         The name of the field.
     * @param defaultValue The default value to return if field is not found or not of
     *                     appropriate type.
     * @return The value of a field within this as a {@code String}
     */
    public String getFieldMLString(COSName name, String defaultValue) {
        COSObject value = cosGetField(name);

        // be lazy about COSString and COSName
        if (value.isNull()) {
            return defaultValue;
        }
        COSString string = value.asString();
        if (string != null) {
            return string.multiLineStringValue();
        }
        return value.stringValue();
    }

    /**
     * The value of a field within this as a {@code String} or the
     * {@code defaultValue} if not found or not a {@link COSString}.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name         The name of the field.
     * @param defaultValue The default value to return if field is not found or not of
     *                     appropriate type.
     * @return The value of a field within this as a {@code String}
     */
    public String getFieldString(COSName name, String defaultValue) {
        COSObject value = cosGetField(name);

        // be lazy about COSString and COSName
        if (value.isNull()) {
            return defaultValue;
        }
        return value.stringValue();
    }

    /*
     * provide some hook for initialization after creation based on a cos object
     */
    protected void initializeFromCos() {
        // do nothing by default
    }

    /*
     * provide some hook for initialization after creation from scratch
     */
    protected void initializeFromScratch() {
        // do nothing by default
    }

    /**
     * Invalidate all local caches as the base object may have changed.
     */
    public void invalidateCaches() {
        // nothing cached here
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.tools.component.IAttributeSupport#removeAttribute(java.lang
     * .Object)
     */
    @Override
    public Object removeAttribute(Object key) {
        return attributeSupport.removeAttribute(new TaggedAttribute(key, getClass()));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.tools.component.IAttributeSupport#setAttribute(java.lang.
     * Object, java.lang.Object)
     */
    @Override
    public Object setAttribute(Object key, Object value) {
        return attributeSupport.setAttribute(new TaggedAttribute(key, getClass()), value);
    }

    /**
     * Set the value of field {@code name}within this.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name  The name of the field.
     * @param value The new value of the field.
     * @return The previously associated COSObject is returned.
     */
    public COSObject setFieldBoolean(COSName name, boolean value) {
        COSBoolean cosValue = COSBoolean.create(value);
        return cosSetField(name, cosValue);
    }

    /**
     * Set the value of field {@code name}within this.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name  The name of the field.
     * @param value The new value of the field.
     * @return The previously associated COSObject is returned.
     */
    public COSObject setFieldFixed(COSName name, float value) {
        COSNumber cosValue = COSFixed.create(value);
        return cosSetField(name, cosValue);
    }

    /**
     * Set the value of field {@code name}within this.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param key   The name of the field.
     * @param array The new value of the field.
     * @return The previously associated COSObject is returned.
     */
    protected COSObject setFieldFixedArray(COSName key, float[] array) {
        if ((array == null) || (array.length == 0)) {
            return cosRemoveField(key);
        }
        // todo 3 reuse existing array?
        COSArray cosArray = COSArray.create();
        for (int i = 0; i < array.length; i++) {
            cosArray.add(COSFixed.create(array[i]));
        }
        return cosSetField(key, cosArray);
    }

    /**
     * Set the value of field {@code name}within this.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name  The name of the field.
     * @param value The new value of the field.
     * @return The previously associated COSObject is returned.
     */
    public COSObject setFieldInt(COSName name, int value) {
        COSNumber cosValue = COSInteger.create(value);
        return cosSetField(name, cosValue);
    }

    /**
     * Set the value of field {@code name}within this.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name  The name of the field.
     * @param value The new value of the field.
     * @return The previously associated COSObject is returned.
     */
    public COSObject setFieldMLString(COSName name, String value) {
        if (value == null) {
            return cosRemoveField(name);
        } else {
            COSString cosValue = COSString.createMultiLine(value);
            return cosSetField(name, cosValue);
        }
    }

    /**
     * Set the value of field {@code name}within this.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name  The name of the field.
     * @param value The new value of the field.
     * @return The previously associated COSObject is returned.
     */
    public COSObject setFieldName(COSName name, String value) {
        if (value == null) {
            return cosRemoveField(name);
        } else {
            COSName cosValue = COSName.create(value.getBytes());
            return cosSetField(name, cosValue);
        }
    }

    /**
     * Set the value of field {@code name}within this.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name  The name of the field.
     * @param value The new value of the field.
     * @return The previously associated COSObject is returned.
     */
    public COSObject setFieldObject(COSName name, COSBasedObject value) {
        if (value == null) {
            return cosRemoveField(name);
        } else {
            return cosSetField(name, value.cosGetObject());
        }
    }

    /**
     * Set the value of field {@code name}within this.
     * <p>
     * This method requires the base object to be a {@link COSDictionary}.
     *
     * @param name  The name of the field.
     * @param value The new value of the field.
     * @return The previously associated COSObject is returned.
     */
    public COSObject setFieldString(COSName name, String value) {
        if (value == null) {
            return cosRemoveField(name);
        } else {
            COSString cosValue = COSString.create(value);
            return cosSetField(name, cosValue);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (cosGetObject() != null) {
            return cosGetObject().toString();
        } else {
            return super.toString();
        }
    }
}
