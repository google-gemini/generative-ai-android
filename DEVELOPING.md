# Developing

## Setting up the repo

### Git Hooks

This repo uses git hooks for certain behaviors such as formatting before a push. To install the git
hooks, run the following command at the root of your repo directory:

```bash
./INSTALL_HOOKS.sh
```

## Building and Releasing

To locally publish the m2 repo:

`./gradlew generativeai:publishToMavenLocal`

To generate a releasable m2 repo:

`./gradlew generativeai:publishAllPublicationsToMavenRepository`

The m2 repo will be in `generativeai/m2`.

To generate Dokka:

`./gradlew generativeai:dokkaHtml`

The docs will be in `generativeai/build/dokka/html`.

## Making changes

When making changes that are intended to invoke a release, it's important to make sure a proper
changelog entry accompanies your change. We use a custom [plugin](./plugins/README.md), alongside an
executable bash script called `change` to better facilitate this process.

Changes are organized in the `.changes` directory at the root of the repo, and each change pending
a release will be represented as a single file with a seemingly random string of words as a file
name. This file will contain two vital pieces of information;

 - What the change is about (a user displayable message to use in the subsequent release notes)
 - The api impact of the change (will this require a major, minor, or patch bump?)

We have tooling that will automatically determine the impact of your changes, so all you need to do
is run the following command from the root of the repo whenever you want to make a change that will
invoke a release:

```bash
change "hello world!"
```

Alternatively, if you want to invoke a release without adding anything to the release notes come
release time- you can run `change` without specifying a message:

```bash
change
```

Both of these commands should generate a new file under the `.changes` directory with your message
and impact.


To learn more, read the section on our changelog plugin in our [plugin readme](./plugins/README.md).

