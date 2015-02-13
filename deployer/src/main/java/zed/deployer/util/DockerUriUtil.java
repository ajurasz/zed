package zed.deployer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DockerUriUtil {
    public static String imageName(String prefix, String uri) {
        Pattern pattern = Pattern.compile(Pattern.quote(prefix) + "(.*?)" + "(\\?|$)");
        Matcher matcher = pattern.matcher(uri);

        if (!matcher.find()) {
            throw new IllegalArgumentException(uri + " is not a valid docker deploy URI. Proper URI format is docker:imagerepoprefix/image[:tag] .");
        }

        return matcher.group(1);
    }

    public static String[] environmentVariables(String uri) {
        String[] splitted = uri.split("\\?");
        if (splitted.length == 1) {
            return new String[]{};
        }

        List<String> envs = new ArrayList<>();
        Pattern p = Pattern.compile(Pattern.quote("e:") + "(.*?)" + "(&|$)");
        Matcher m = p.matcher(splitted[1]);
        while (m.find()) {
            envs.add(m.group(1));
        }

        return envs.toArray(new String[]{});
    }
}
