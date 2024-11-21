<!-- Plugin description -->
# IdeaVim Switch

An IdeaVim extension inspired from [switch.vim](https://github.com/AndrewRadev/switch.vim)
to switch between related text segments.

## Installation

1. Install [IdeaVim](https://plugins.jetbrains.com/plugin/164-ideavim) if you
   haven't already
2. Install this plugin:
    - In IntelliJ IDEA: Settings/Preferences → Plugins → Marketplace → Search
      for "Vim-Switch"
    - Or download
      from [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/25899-vim-switch)
3. Add `set switch` to your `~/.ideavimrc` file, then run `:source ~/.ideavimrc` or restart the IDE.

## Usage

Place your cursor on a term and use:
- `:Switch` to cycle forward through alternatives
- `:SwitchReverse` to cycle backward

You might want to map these commands, for example:
```vim
" Map to <leader>s and <leader>S
nmap <leader>s :Switch<CR>
nmap <leader>S :SwitchReverse<CR>

" Or use - and +
nmap - :Switch<CR>
nmap + :SwitchReverse<CR>
```

## Configuration

Enable patterns in your `.ideavimrc` either by group or individual patterns:
```vim
" Enable all patterns from multiple groups
let g:switch_definitions = 'group:basic,group:java,group:rspec'

" Enable specific patterns
let g:switch_definitions = 'basic_true_false,java_visibility,rspec_should'

" Mix groups and individual patterns
let g:switch_definitions = 'group:basic,java_visibility,rspec_should'
```

## Built-in Patterns

### Basic (`group:basic`)
- Binary (`basic_binary`): `0` ↔ `1`
- Directional (`basic_up_down_left_right`): `up` ↔ `down` ↔ `left` ↔ `right` (case insensitive)
- Logical operators (`basic_logical_ops`): `&&` ↔ `||`
- Bitwise operators (`basic_bitwise_ops`): `&` ↔ `|`
- Boolean (`basic_true_false`): `true` ↔ `false` (case-insensitive)
- Boolean operators (`basic_and_or`): `and` ↔ `or` (case-insensitive)
- Equality (`basic_equality`): `==` ↔ `!=`
- Is checks (`basic_is_is_not`): `is` ↔ `is not`
- Single/double quotes (`basic_quotes`): `"string"` ↔ `'string'` ↔ ``` `string` ``` 
 
### Java (`group:java`)
- Assertions (`java_assert_equals`): `assertEquals` ↔ `assertNotEquals`
- Boolean assertions (`java_assert_true_false`): `assertTrue` ↔ `assertFalse`
- Null assertions (`java_assert_null`): `assertNull` ↔ `assertNotNull`
- Visibility (`java_visibility`): `private` ↔ `protected` ↔ `public`
- Optional checks (`java_optional_check`): `isPresent()` ↔ `isEmpty()`

### JavaScript (`group:javascript`)
- Functions (`javascript_function`): Transform between function syntaxes
    - `function name() {` ↔ `const name = () => {`
    - `async function name() {` ↔ `const name = async () => {`
- Arrow functions (`javascript_arrow_function`): `function(x) {` ↔ `(x) => {`
- Variable declarations (`javascript_es6_declarations`): `var` ↔ `let` ↔ `const`

### RSpec (`group:rspec`)
RSpec patterns:
- Should syntax (`rspec_should`): `should` ↔ `should_not`
- Expectation chains (`rspec_to`): `.to` ↔ `.not_to` ↔ `.to_not`
- Truthiness (`rspec_be_truthy_falsey`): `be_truthy` ↔ `be_falsey`
- Presence (`rspec_be_present_blank`): `be_present` ↔ `be_blank`

### Ruby (`group:ruby`)
- Hash syntax (`ruby_hash_style`): `:key => value` ↔ `key: value`
- One-line hash (`ruby_oneline_hash`): Similar to hash_style but for compact syntax
- Lambda syntax (`ruby_lambda`):
    - `lambda { |x| }` ↔ `-> (x) { }`
    - `lambda { }` ↔ `-> { }`
- Conditionals (`ruby_if_clause`):
    - `if condition` ↔ `if true or (condition)`
    - `if true or (condition)` ↔ `if false and (condition)`
    - `if false and (condition)` ↔ `if condition`
- Array syntax (`ruby_array_shorthand`): `['a', 'b']` ↔ `%w(a b)`
- Hash access (`ruby_fetch`): `hash[key]` ↔ `hash.fetch(key)`
- Nil assertions (`ruby_assert_nil`): `assert_nil x` ↔ `assert_equal nil, x`

### Rust (`group:rust`)
- Void type checks (`rust_void_typecheck`):
    - `let x =` ↔ `let x: () =`
- Turbofish (`rust_turbofish`):
    - `parse()` ↔ `parse::<Todo>()`
- String types (`rust_string`):
    - `"text"` ↔ `r"text"` ↔ `r#"text"#`
- Optional checks (`rust_is_some`): `is_some` ↔ `is_none`
- Assertions (`rust_assert`): `assert_eq!` ↔ `assert_ne!`
- Cargo dependencies (`cargo_dependency_version`):
    - `package = "1.0"` ↔ `package = { version = "1.0" }`

### Scala (`group:scala`)
String interpolation (`scala_string`), cycles through:
- Regular strings: `"text"`
- String interpolation: `s"text"`
- Formatted strings: `f"text"`
- Multiline strings: `"""text"""`
- And combinations: `s"""text"""`, `f"""text"""`

### Markdown (`group:markdown`)
- Task items (`markdown_task_item`): `- [ ] Buy milk` ↔ `- [x] Buy milk`

## Custom Patterns

Define your own patterns using the provided VimScript functions:

```vim
let g:switch_custom_definitions = [
    " Basic word cycling (with word boundaries)
    \ switchWords(['debug', 'info', 'warn', 'error']),
    
    " Case-insensitive word cycling (with word boundaries)
    \ switchNormalizedCaseWords(['GET', 'POST', 'PUT', 'DELETE']),
    
    " Custom regex patterns (each pattern is a pair of [match, replacement])
    \ ['width:\s*(\d+)px', 'width: \1em'],
    \ ['height:\s*(\d+)px', 'height: \1em']
\ ]
```

Available functions:
- `switchWords(words)`: Creates patterns that cycle through words in order, matching exact words with word boundaries
    - Example: `switchWords(['yes', 'no'])` matches exactly 'yes' or 'no' and cycles between them

- `switchNormalizedCaseWords(words)`: Like `switchWords` but case-insensitive
    - Example: `switchNormalizedCaseWords(['GET', 'POST'])` matches 'GET'/'get'/'Get' and cycles to 'POST'/'post'/'Post' preserving the original case

<!-- Plugin description end -->
