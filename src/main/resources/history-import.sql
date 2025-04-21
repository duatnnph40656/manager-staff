-- Create import_history table if it doesn't exist
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[import_history]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[import_history](
    [id] [bigint] IDENTITY(1,1) NOT NULL,
    [file_name] [nvarchar](255) NOT NULL,
    [total_records] [int] NULL,
    [success_records] [int] NULL,
    [failed_records] [int] NULL,
    [errors] [nvarchar](max) NULL,
    [import_date] [datetime2](7) NOT NULL,
    [imported_by] [nvarchar](255) NOT NULL,
    CONSTRAINT [PK_import_history] PRIMARY KEY CLUSTERED 
    (
        [id] ASC
    )
)
END; 