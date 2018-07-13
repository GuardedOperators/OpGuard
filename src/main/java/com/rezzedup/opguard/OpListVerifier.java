package com.rezzedup.opguard;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public final class OpListVerifier
{
    public static final Path DATA_DIRECTORY = OpGuard.PLUGIN_DIRECTORY.resolve(".opdata");
    public static final Path PASSWORD_FILE = DATA_DIRECTORY.resolve(".password");
    public static final Path VERIFIED_FILE = DATA_DIRECTORY.resolve(".verified");
    
    private final Set<UUID> verified = new LinkedHashSet<>();
}
