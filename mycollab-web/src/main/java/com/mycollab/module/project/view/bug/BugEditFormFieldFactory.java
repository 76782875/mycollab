/**
 * This file is part of mycollab-web.
 *
 * mycollab-web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-web.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mycollab.module.project.view.bug;

import com.mycollab.common.i18n.ErrorI18nEnum;
import com.mycollab.common.i18n.GenericI18Enum;
import com.mycollab.module.project.ProjectTypeConstants;
import com.mycollab.module.project.domain.SimpleProjectMember;
import com.mycollab.module.project.i18n.BugI18nEnum;
import com.mycollab.module.project.ui.components.ProjectSubscribersComp;
import com.mycollab.module.project.ui.form.ProjectFormAttachmentUploadField;
import com.mycollab.module.project.view.bug.components.BugPriorityComboBox;
import com.mycollab.module.project.view.bug.components.BugSeverityComboBox;
import com.mycollab.module.project.view.milestone.MilestoneComboBox;
import com.mycollab.module.project.view.settings.component.ComponentMultiSelectField;
import com.mycollab.module.project.view.settings.component.ProjectMemberSelectionField;
import com.mycollab.module.project.view.settings.component.VersionMultiSelectField;
import com.mycollab.module.tracker.domain.BugWithBLOBs;
import com.mycollab.module.tracker.domain.SimpleBug;
import com.mycollab.vaadin.AppContext;
import com.mycollab.vaadin.ui.AbstractBeanFieldGroupEditFieldFactory;
import com.mycollab.vaadin.ui.GenericBeanForm;
import com.mycollab.vaadin.web.ui.DoubleField;
import com.mycollab.vaadin.web.ui.field.DateTimeOptionField;
import com.vaadin.data.Property;
import com.vaadin.ui.Field;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;

/**
 * @author MyCollab Ltd
 * @since 5.2.0
 */
class BugEditFormFieldFactory extends AbstractBeanFieldGroupEditFieldFactory<SimpleBug> {
    private static final long serialVersionUID = 1L;

    private ComponentMultiSelectField componentSelect;
    private VersionMultiSelectField affectedVersionSelect;
    private VersionMultiSelectField fixedVersionSelect;
    private ProjectSubscribersComp subscribersComp;
    private ProjectFormAttachmentUploadField attachmentUploadField;

    BugEditFormFieldFactory(GenericBeanForm<SimpleBug> form, Integer prjId) {
        super(form);
        subscribersComp = new ProjectSubscribersComp(false, prjId, AppContext.getUsername());
    }

    @Override
    protected Field<?> onCreateField(final Object propertyId) {
        final SimpleBug beanItem = attachForm.getBean();
        if (propertyId.equals("environment")) {
            return new RichTextArea();
        } else if (propertyId.equals("description")) {
            return new RichTextArea();
        } else if (propertyId.equals("priority")) {
            return new BugPriorityComboBox();
        } else if (propertyId.equals("assignuser")) {
            ProjectMemberSelectionField field = new ProjectMemberSelectionField();
            field.addValueChangeListener(valueChangeEvent -> {
                Property property = valueChangeEvent.getProperty();
                SimpleProjectMember member = (SimpleProjectMember) property.getValue();
                if (member != null) {
                    subscribersComp.addFollower(member.getUsername());
                }
            });
            return field;
        } else if (propertyId.equals("id")) {
            attachmentUploadField = new ProjectFormAttachmentUploadField();
            if (beanItem.getId() != null) {
                attachmentUploadField.getAttachments(beanItem.getProjectid(), ProjectTypeConstants.BUG, beanItem.getId());
            }
            return attachmentUploadField;
        } else if (propertyId.equals("severity")) {
            return new BugSeverityComboBox();
        } else if (propertyId.equals("components")) {
            componentSelect = new ComponentMultiSelectField();
            return componentSelect;
        } else if (propertyId.equals("affectedVersions")) {
            affectedVersionSelect = new VersionMultiSelectField();
            return affectedVersionSelect;
        } else if (propertyId.equals("fixedVersions")) {
            fixedVersionSelect = new VersionMultiSelectField();
            return fixedVersionSelect;
        } else if (propertyId.equals("summary")) {
            final TextField tf = new TextField();
            if (isValidateForm) {
                tf.setNullRepresentation("");
                tf.setRequired(true);
                tf.setRequiredError(AppContext.getMessage(ErrorI18nEnum.FIELD_MUST_NOT_NULL,
                        AppContext.getMessage(BugI18nEnum.FORM_SUMMARY)));
            }

            return tf;
        } else if (propertyId.equals("milestoneid")) {
            final MilestoneComboBox milestoneBox = new MilestoneComboBox();
            milestoneBox.addValueChangeListener(valueChangeEvent -> {
                String milestoneName = milestoneBox.getItemCaption(milestoneBox.getValue());
                beanItem.setMilestoneName(milestoneName);
            });
            return milestoneBox;
        } else if (propertyId.equals("estimatetime") || (propertyId.equals("estimateremaintime"))) {
            return new DoubleField();
        } else if (propertyId.equals("selected")) {
            return subscribersComp;
        } else if (BugWithBLOBs.Field.startdate.equalTo(propertyId) || BugWithBLOBs.Field.enddate.equalTo(propertyId)
                || BugWithBLOBs.Field.duedate.equalTo(propertyId)) {
            return new DateTimeOptionField(true);
        }

        return null;
    }

    public ComponentMultiSelectField getComponentSelect() {
        return componentSelect;
    }

    public VersionMultiSelectField getAffectedVersionSelect() {
        return affectedVersionSelect;
    }

    public VersionMultiSelectField getFixedVersionSelect() {
        return fixedVersionSelect;
    }

    public ProjectSubscribersComp getSubscribersComp() {
        return subscribersComp;
    }

    public ProjectFormAttachmentUploadField getAttachmentUploadField() {
        return attachmentUploadField;
    }
}
