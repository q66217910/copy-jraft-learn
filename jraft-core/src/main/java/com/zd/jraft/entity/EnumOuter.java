package com.zd.jraft.entity;

import com.google.protobuf.Descriptors;

public class EnumOuter {

    private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

    static {
        java.lang.String[] descriptorData = {"\n\nenum.proto\022\005jraft*l\n\tEntryType\022\026\n\022ENTR"
                + "Y_TYPE_UNKNOWN\020\000\022\024\n\020ENTRY_TYPE_NO_OP\020\001\022\023"
                + "\n\017ENTRY_TYPE_DATA\020\002\022\034\n\030ENTRY_TYPE_CONFIG"
                + "URATION\020\003*\227\001\n\tErrorType\022\023\n\017ERROR_TYPE_NO"
                + "NE\020\000\022\022\n\016ERROR_TYPE_LOG\020\001\022\025\n\021ERROR_TYPE_S"
                + "TABLE\020\002\022\027\n\023ERROR_TYPE_SNAPSHOT\020\003\022\034\n\030ERRO"
                + "R_TYPE_STATE_MACHINE\020\004\022\023\n\017ERROR_TYPE_MET"
                + "A\020\005B*\n\034com.alipay.sofa.jraft.entityB\nEnu" + "mOutter"};
        com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            public com.google.protobuf.ExtensionRegistry assignDescriptors(com.google.protobuf.Descriptors.FileDescriptor root) {
                descriptor = root;
                return null;
            }
        };
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData,
                new com.google.protobuf.Descriptors.FileDescriptor[]{}, assigner);
    }

    private EnumOuter() {
    }

    public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
        registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
    }

    public enum EntryType implements com.google.protobuf.ProtocolMessageEnum {
        /**
         * <code>ENTRY_TYPE_UNKNOWN = 0;</code>
         */
        ENTRY_TYPE_UNKNOWN(0),
        /**
         * <code>ENTRY_TYPE_NO_OP = 1;</code>
         */
        ENTRY_TYPE_NO_OP(1),
        /**
         * <code>ENTRY_TYPE_DATA = 2;</code>
         */
        ENTRY_TYPE_DATA(2),
        /**
         * <code>ENTRY_TYPE_CONFIGURATION = 3;</code>
         */
        ENTRY_TYPE_CONFIGURATION(3);

        public static EntryType forNumber(int value) {
            switch (value) {
                case 0:
                    return ENTRY_TYPE_UNKNOWN;
                case 1:
                    return ENTRY_TYPE_NO_OP;
                case 2:
                    return ENTRY_TYPE_DATA;
                case 3:
                    return ENTRY_TYPE_CONFIGURATION;
                default:
                    return null;
            }
        }

        private final int value;

        private static final EntryType[] VALUES = values();

        public static EntryType valueOf(com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
                throw new java.lang.IllegalArgumentException("EnumValueDescriptor is not for this type.");
            }
            return VALUES[desc.getIndex()];
        }

        public static com.google.protobuf.Internal.EnumLiteMap<EntryType> internalGetValueMap() {
            return internalValueMap;
        }

        private static final com.google.protobuf.Internal.EnumLiteMap<EntryType> internalValueMap = new com.google.protobuf.Internal.EnumLiteMap<EntryType>() {
            public EntryType findValueByNumber(int number) {
                return EntryType
                        .forNumber(number);
            }
        };

        private EntryType(int value) {
            this.value = value;
        }

        @Override
        public int getNumber() {
            return value;
        }

        @Override
        public Descriptors.EnumValueDescriptor getValueDescriptor() {
            return getDescriptor().getValues().get(ordinal());
        }

        @Override
        public Descriptors.EnumDescriptor getDescriptorForType() {
            return getDescriptor();
        }

        public static com.google.protobuf.Descriptors.EnumDescriptor getDescriptor() {
            return EnumOuter.getDescriptor().getEnumTypes().get(0);
        }
    }
}
