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
package de.intarsys.pdf.app.appearance;

import de.intarsys.pdf.cos.COSName;
import de.intarsys.tools.provider.ProviderTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A registry for the available {@link IAppearanceCreator} strategies. The
 * {@link IAppearanceCreator} is looked up by the annotation sub-type.
 */
public class StandardAppearanceCreatorRegistry implements IAppearanceCreatorRegistry {
    private Map<COSName, IAppearanceCreator> instances = new HashMap<COSName, IAppearanceCreator>();

    private boolean initialized = false;

    private boolean lookupProviders = true;

    private static final Logger Log = Logger.getLogger("de.intarsys.pdf.app.appearance");

    protected StandardAppearanceCreatorRegistry() {
        super();
    }

    protected IAppearanceCreator[] findProviders() {
        List<IAppearanceCreator> result = new ArrayList<IAppearanceCreator>();
        Iterator<IAppearanceCreator> ps = ProviderTools.providers(IAppearanceCreator.class);
        while (ps.hasNext()) {
            try {
                result.add(ps.next());
            } catch (Throwable e) {
                Log.log(Level.WARNING, "can't load service provider (" + e.getMessage() + ")");
            }
        }
        return result.toArray(new IAppearanceCreator[result.size()]);
    }

    synchronized public IAppearanceCreator[] getAppearanceCreators() {
        init();
        return instances.values().toArray(new IAppearanceCreator[instances.size()]);
    }

    protected void init() {
        if (!lookupProviders || initialized) {
            return;
        }
        initialized = true;
        IAppearanceCreator[] providers = findProviders();
        for (int i = 0; i < providers.length; i++) {
            IAppearanceCreator provider = providers[i];
            registerAppearanceCreator(provider);
        }
    }

    public boolean isLookupProviders() {
        return lookupProviders;
    }

    synchronized public IAppearanceCreator lookupAppearanceCreator(COSName type) {
        init();
        return instances.get(type);
    }

    synchronized public void registerAppearanceCreator(IAppearanceCreator creator) {
        instances.put(creator.getAnnotationType(), creator);
    }

    public void setLookupProviders(boolean lookupProviders) {
        this.lookupProviders = lookupProviders;
    }

    synchronized public void unregisterAppearanceCreator(IAppearanceCreator creator) {
        instances.remove(creator.getAnnotationType());
    }
}
