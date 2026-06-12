package id.xtramile.validator.web.messages;

import id.xtramile.validator.enums.Group;
import id.xtramile.validator.web.AnnotationRegistry;
import id.xtramile.validator.web.MessageResourceResolver;

abstract class AbstractGroupConstraintMessages implements ConstraintMessageResolver {

    protected final MessageResourceResolver messageResolver;
    protected final Group group;

    protected AbstractGroupConstraintMessages(MessageResourceResolver messageResolver, Group group) {
        this.messageResolver = messageResolver;
        this.group = group;
    }

    @Override
    public boolean supports(Class<?> annotationType) {
        return AnnotationRegistry.isInGroup(annotationType, group);
    }
}
