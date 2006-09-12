/*
 * $Id: AbstractFormValidator.java 5771 2006-05-19 12:04:06 +0000 (Fri, 19 May
 * 2006) joco01 $ $Revision$ $Date: 2006-05-19 12:04:06 +0000 (Fri, 19
 * May 2006) $
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.markup.html.form.validation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import wicket.markup.html.form.FormComponent;
import wicket.model.IModel;
import wicket.util.lang.Classes;
import wicket.validation.ValidationError;

/**
 * Base class for {@link wicket.markup.html.form.validation.IFormValidator}s.
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public abstract class AbstractFormValidator implements IFormValidator
{
	/**
	 * DEPRECATED/UNSUPPORTED
	 * 
	 * Gets the default variables for interpolation.
	 * 
	 * @return a map with the variables for interpolation
	 * 
	 * @deprecated use {@link #variablesMap(IValidatable)} instead
	 * @throws UnsupportedOperationException
	 * 
	 * FIXME 2.0: remove asap
	 */
	protected final Map<String, Object> messageModel()
	{
		throw new UnsupportedOperationException("THIS METHOD IS DEPRECATED, SEE JAVADOC");
	}


	/**
	 * Reports an error against validatable using the map returned by
	 * {@link #variablesMap(IValidatable)}for variable interpolations and
	 * message key returned by {@link #resourceKey()}.
	 * 
	 * @param fc
	 *            form component against which the error is reported
	 * 
	 */
	public void error(FormComponent fc)
	{
		error(fc, resourceKey(), variablesMap());
	}

	/**
	 * Reports an error against the validatalbe using the given map for variable
	 * interpolations and message resource key provided by
	 * {@link #resourceKey()}
	 * 
	 * @param fc
	 *            form component against which the error is reported
	 * @param vars
	 *            variables for variable interpolation
	 */
	public void error(FormComponent fc, final Map<String, Object> vars)
	{
		if (vars == null)
		{
			throw new IllegalArgumentException("Argument [[vars]] cannot be null");
		}
		error(fc, resourceKey(), vars);
	}

	/**
	 * Reports an error against the validatable using the specified resource key
	 * and variable map
	 * 
	 * @param fc
	 *            form component against which the error is reported
	 * 
	 * @param validatable
	 *            validatble being validated
	 * @param resourceKey
	 *            The message resource key to use
	 * @param vars
	 *            The model for variable interpolation
	 */
	public void error(FormComponent fc, final String resourceKey, Map<String, Object> vars)
	{
		if (fc == null)
		{
			throw new IllegalArgumentException("Argument [[fc]] cannot be null");
		}
		if (vars == null)
		{
			throw new IllegalArgumentException("Argument [[vars]] cannot be null");
		}
		if (resourceKey == null)
		{
			throw new IllegalArgumentException("Argument [[resourceKey]] cannot be null");
		}


		ValidationError error = new ValidationError().addMessageKey(resourceKey);
		final String defaultKey = Classes.simpleName(getClass());
		if (!resourceKey.equals(defaultKey))
		{
			error.addMessageKey(defaultKey);
		}

		error.setVars(vars);
		fc.error(error);
	}

	/**
	 * Gets the default variables for interpolation. These are for every
	 * component:
	 * <ul>
	 * <li>${input(n)}: the user's input</li>
	 * <li>${name(n)}: the name of the component</li>
	 * <li>${label(n)}: the label of the component - either comes from
	 * FormComponent.labelModel or resource key [form-id].[form-component-id] in
	 * that order</li>
	 * </ul>
	 * 
	 * @return a map with the variables for interpolation
	 */
	protected Map<String, Object> variablesMap()
	{
		FormComponent[] formComponents = getDependentFormComponents();

		if (formComponents != null && formComponents.length > 0)
		{
			Map<String, Object> args = new HashMap<String, Object>(
					formComponents.length * 3);
			for (int i = 0; i < formComponents.length; i++)
			{
				final FormComponent formComponent = formComponents[i];

				String arg = "label" + i;
				IModel label = formComponent.getLabel();
				if (label != null)
				{
					args.put(arg, (Serializable)label.getObject());
				}
				else
				{
					args.put(arg, formComponent.getLocalizer().getString(formComponent.getId(),
							formComponent.getParent(), formComponent.getId()));
				}

				args.put("input" + i, formComponent.getInput());
				args.put("name" + i, formComponent.getId());
			}
			return args;
		}
		else
		{
			return new HashMap<String, Object>(2);
		}
	}

	/**
	 * Gets the resource key for validator's error message from the
	 * ApplicationSettings class.
	 * 
	 * @return the resource key based on the form component
	 */
	protected String resourceKey()
	{
		return Classes.simpleName(getClass());
	}
}