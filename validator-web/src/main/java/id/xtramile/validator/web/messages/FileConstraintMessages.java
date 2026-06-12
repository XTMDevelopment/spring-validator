package id.xtramile.validator.web.messages;

import id.xtramile.validator.annotation.file.ValidFileExtension;
import id.xtramile.validator.annotation.file.ValidFileMimeType;
import id.xtramile.validator.annotation.file.ValidFileSize;
import id.xtramile.validator.annotation.file.ValidImageDimensions;
import id.xtramile.validator.web.MessageResourceResolver;

import java.util.Map;

import static id.xtramile.validator.enums.Group.FILE;

final class FileConstraintMessages extends AbstractGroupConstraintMessages {

    FileConstraintMessages(MessageResourceResolver messageResolver) {
        super(messageResolver, FILE);
    }

    @Override
    public String resolve(String field, Class<?> type, Map<String, Object> attrs, Class<?> dtoClass) {
        if (type == ValidImageDimensions.class) {
            Integer minW = (Integer) attrs.get("minWidth");
            Integer minH = (Integer) attrs.get("minHeight");
            Integer maxW = (Integer) attrs.get("maxWidth");
            Integer maxH = (Integer) attrs.get("maxHeight");

            return messageResolver.getMessage(FILE, "image-dimension", field, minW, minH, maxW, maxH);
        }

        if (type == ValidFileExtension.class) {
            String[] extensions = (String[]) attrs.get("allowed");
            String extList = String.join(", ", extensions);

            return messageResolver.getMessage(FILE, "file-extension", field, extList);
        }

        if (type == ValidFileMimeType.class) {
            String[] mimeTypes = (String[]) attrs.get("allowed");
            String mimeList = String.join(", ", mimeTypes);

            return messageResolver.getMessage(FILE, "mime-type", field, mimeList);
        }

        if (type == ValidFileSize.class) {
            Long minBytes = (Long) attrs.getOrDefault("minBytes", 0L);
            Long maxBytes = (Long) attrs.getOrDefault("maxBytes", Long.MAX_VALUE);

            return messageResolver.getMessage(FILE, "file-size", field, minBytes, maxBytes);
        }

        return null;
    }
}
