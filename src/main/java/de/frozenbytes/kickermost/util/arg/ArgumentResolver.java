package de.frozenbytes.kickermost.util.arg;


import com.google.common.base.Preconditions;

public final class ArgumentResolver {

    private ArgumentResolver(){
    }

    public static String resolveConfigFilePath(final String[] args){
        try{
            Preconditions.checkNotNull(args, "args should not be null!");
            Preconditions.checkArgument(args.length > 0, "args should not be empty!");
            Preconditions.checkArgument(args[0] != null && args[0].equals("-config"), String.format("Expected the first argument to be -config, but was: '%s'!", args[0]));
            Preconditions.checkArgument(args[1] != null && !args[1].trim().isEmpty(), "No filepath could be found for argument -config!");
            return args[1];
        }catch (RuntimeException e){
            throw new IllegalStateException("Please define the path to the config file by using the argument -config!", e);
        }
    }

}
