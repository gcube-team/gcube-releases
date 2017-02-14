INSERT INTO public."Version"(
            "VER_ID", "VER_Version", "VER_Upgradetime")
    VALUES (uuid_generate_v1mc(), '4.2.0', now());
