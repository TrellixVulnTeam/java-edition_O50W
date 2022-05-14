use anyhow::{Context, Result};
use xshell::{cmd, Shell};

fn main() -> Result<()> {
    let sh = Shell::new()?;

    cmd!(sh, "cargo fmt --all")
        .run()
        .context("Failed to format all the code.")?;
    cmd!(sh, "cargo clippy --workspace --fix --allow-dirty --all-targets --all-features -- -D warnings -A clippy::type_complexity -W clippy::doc_markdown")
        .run()
        .context("Please fix clippy errors in output above.")?;
    Ok(())
}
