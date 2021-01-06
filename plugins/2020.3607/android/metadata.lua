local metadata =
{
    plugin =
    {
        format = 'jar',
        manifest = 
        {
            permissions = {},
            usesPermissions =
            {
                "android.permission.INTERNET",
            },
            usesFeatures = 
            {
            },
            applicationChildElements =
            {
            }
        }
    },
    
    coronaManifest = {
        dependencies = {
            ["shared.google.play.services.ads"] = "com.coronalabs",
            ["shared.google.play.services.base"] = "com.coronalabs",
        }
    }
}

return metadata
